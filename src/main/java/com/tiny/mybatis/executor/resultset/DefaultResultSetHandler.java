package com.tiny.mybatis.executor.resultset;

import com.tiny.mybatis.executor.Executor;
import com.tiny.mybatis.executor.parameter.ParameterHandler;
import com.tiny.mybatis.executor.result.DefaultResultContext;
import com.tiny.mybatis.executor.result.DefaultResultHandler;
import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.mapping.ResultMap;
import com.tiny.mybatis.mapping.ResultMapping;
import com.tiny.mybatis.reflection.ReflectorFactory;
import com.tiny.mybatis.reflection.factory.ObjectFactory;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.session.ResultContext;
import com.tiny.mybatis.session.ResultHandler;
import com.tiny.mybatis.session.RowBounds;
import com.tiny.mybatis.type.TypeHandler;
import com.tiny.mybatis.type.TypeHandlerRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DefaultResultSetHandler implements ResultSetHandler {

    private final Executor executor;
    private final Configuration configuration;
    private final MappedStatement mappedStatement;
    private final RowBounds rowBounds;
    private final ParameterHandler parameterHandler;
    private final ResultHandler<?> resultHandler;
    private final BoundSql boundSql;
    private final TypeHandlerRegistry typeHandlerRegistry;

    private final ObjectFactory objectFactory;
    private final ReflectorFactory reflectorFactory;


    public DefaultResultSetHandler(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler<?> resultHandler, BoundSql boundSql,
                                   RowBounds rowBounds) {
        this.executor = executor;
        this.configuration = mappedStatement.getConfiguration();
        this.mappedStatement = mappedStatement;
        this.rowBounds = rowBounds;
        this.parameterHandler = parameterHandler;
        this.boundSql = boundSql;
        this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
        this.objectFactory = configuration.getObjectFactory();
        this.reflectorFactory = configuration.getReflectorFactory();

        this.resultHandler = resultHandler;
    }

    @Override
    public List<Object> handleResultSets(Statement stmt) throws SQLException {

        final List<Object> multipleResults = new ArrayList<>();

        int resultSetCount = 0;
        ResultSetWrapper rsw = getFirstResultSet(stmt);

        List<ResultMap> resultMaps = mappedStatement.getResultMaps();
        int resultMapCount = resultMaps.size();
        while (rsw != null && resultMapCount > resultSetCount) {
            ResultMap resultMap = resultMaps.get(resultSetCount);
            handleResultSet(rsw, resultMap, multipleResults, null);
            rsw = getNextResultSet(stmt);
            resultSetCount++;
        }
        return collapseSingleResultList(multipleResults);
    }

    private List<Object> collapseSingleResultList(List<Object> multipleResults) {
        return multipleResults.size() == 1 ? (List<Object>) multipleResults.get(0) : multipleResults;
    }

    private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap, List<Object> multipleResults, ResultMapping parentMapping) throws SQLException {

        try {
            DefaultResultHandler defaultResultHandler = new DefaultResultHandler(objectFactory);
            handleRowValues(rsw, resultMap, defaultResultHandler, rowBounds, null);
            multipleResults.add(defaultResultHandler.getResultList());
        }finally {
            // issue #228 (close resultsets)
            closeResultSet(rsw.getResultSet());
        }


    }

    public void handleRowValues(ResultSetWrapper rsw, ResultMap resultMap, ResultHandler<?> resultHandler, RowBounds rowBounds, ResultMapping parentMapping) throws SQLException {
        DefaultResultContext<Object> resultContext = new DefaultResultContext<>();
        ResultSet resultSet = rsw.getResultSet();
        skipRows(resultSet, rowBounds);
        while (shouldProcessMoreRows(resultContext, rowBounds) && !resultSet.isClosed() && resultSet.next()) {
            ResultMap discriminatedResultMap = resultMap;
            Object rowValue = getRowValue(rsw, discriminatedResultMap, null);
            storeObject(resultHandler, resultContext, rowValue, parentMapping, resultSet);
        }
    }

    private void storeObject(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue, ResultMapping parentMapping, ResultSet rs) throws SQLException {
        if (parentMapping != null) {

        } else {
            callResultHandler(resultHandler, resultContext, rowValue);
        }
    }
    private void callResultHandler(ResultHandler<?> resultHandler, DefaultResultContext<Object> resultContext, Object rowValue) {
        resultContext.nextResultObject(rowValue);
        ((ResultHandler<Object>) resultHandler).handleResult(resultContext);
    }


    private boolean shouldProcessMoreRows(ResultContext<?> context, RowBounds rowBounds) {
        return !context.isStopped() && context.getResultCount() < rowBounds.getLimit();
    }

    private void skipRows(ResultSet rs, RowBounds rowBounds) throws SQLException {
        if (rs.getType() != ResultSet.TYPE_FORWARD_ONLY) {
            if (rowBounds.getOffset() != RowBounds.NO_ROW_OFFSET) {
                rs.absolute(rowBounds.getOffset());
            }
        } else {
            for (int i = 0; i < rowBounds.getOffset(); i++) {
                if (!rs.next()) {
                    break;
                }
            }
        }
    }

    private Object getRowValue(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix) throws SQLException {
        Object rowValue = createResultObject(rsw, resultMap, columnPrefix);
        if (rowValue != null && !hasTypeHandlerForResultObject(rsw, resultMap.getType())) {
            List<UnMappedColumnAutoMapping> autoMapping = createAutomaticMappings(rsw, resultMap, rowValue, columnPrefix);
            if (!autoMapping.isEmpty()) {
                for (UnMappedColumnAutoMapping mapping : autoMapping) {
                    final Object value = mapping.typeHandler.getResult(rsw.getResultSet(), mapping.column);
                    if (value != null) {
                        setValue(mapping.property, value,resultMap.getType(),rowValue);
                    }

                }
            }
        }
        return rowValue;
    }

    private void setValue(String property,Object value,Class<?> type,Object rowValue){
        Method[] declaredMethods = type.getDeclaredMethods();
        if (declaredMethods != null) {
            for (Method method : declaredMethods) {
                if(methodToProperty(method.getName()).equals(property)&&method.getName().startsWith("set")){
                    try {
                        method.invoke(rowValue,value);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String methodToProperty(String name) {
        if (name.startsWith("is")) {
            name = name.substring(2);
        } else if (name.startsWith("get") || name.startsWith("set")) {
            name = name.substring(3);
        }

        if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
            name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
        }

        return name;
    }

    private List<UnMappedColumnAutoMapping> createAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, Object rowValue, String columnPrefix) throws SQLException {
        List<UnMappedColumnAutoMapping> autoMapping = new ArrayList<>();
        final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
        for (String columnName : unmappedColumnNames) {
            String property = columnName;
            Class<?> type = resultMap.getType();
            Field declaredField = null;
            try {
                declaredField = type.getDeclaredField(property);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            Class<?> propertyType = declaredField.getDeclaringClass();
            final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
            autoMapping.add(new UnMappedColumnAutoMapping(columnName, property, typeHandler, propertyType.isPrimitive()));
        }
        return autoMapping;
    }

    private boolean hasTypeHandlerForResultObject(ResultSetWrapper rsw, Class<?> resultType) {
        if (rsw.getColumnNames().size() == 1) {
            return typeHandlerRegistry.hasTypeHandler(resultType, rsw.getJdbcType(rsw.getColumnNames().get(0)));
        }
        return typeHandlerRegistry.hasTypeHandler(resultType);
    }



    private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix) throws SQLException {
        if(hasPrimitiveForResultObject(rsw,resultMap.getType())){
            System.out.println("=====================基础类型");
            final Class<?> resultType = resultMap.getType();
            final String columnName = rsw.getColumnNames().get(0);
            final TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
            return typeHandler.getResult(rsw.getResultSet(), columnName);
        }
        return objectFactory.create(resultMap.getType());
    }

    private ResultSetWrapper getNextResultSet(Statement stmt) {
        // Making this method tolerant of bad JDBC drivers
        try {
            if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
                // Crazy Standard JDBC way of determining if there are more results
                if (!(!stmt.getMoreResults() && stmt.getUpdateCount() == -1)) {
                    ResultSet rs = stmt.getResultSet();
                    if (rs == null) {
                        return getNextResultSet(stmt);
                    } else {
                        return new ResultSetWrapper(rs, configuration);
                    }
                }
            }
        } catch (Exception e) {
            // Intentionally ignored.
        }
        return null;
    }

    private ResultSetWrapper getFirstResultSet(Statement stmt) throws SQLException {
        ResultSet rs = stmt.getResultSet();
        while (rs == null) {
            // move forward to get the first resultset in case the driver
            // doesn't return the resultset as the first result (HSQLDB 2.1)
            if (stmt.getMoreResults()) {
                rs = stmt.getResultSet();
            } else {
                if (stmt.getUpdateCount() == -1) {
                    // no more results. Must be no resultset
                    break;
                }
            }
        }
        return rs != null ? new ResultSetWrapper(rs, configuration) : null;
    }

    private boolean hasPrimitiveForResultObject(ResultSetWrapper rsw, Class<?> resultType) {
        if (rsw.getColumnNames().size() == 1) {
            return typeHandlerRegistry.hasTypeHandler(resultType, rsw.getJdbcType(rsw.getColumnNames().get(0)));
        }
        return typeHandlerRegistry.hasTypeHandler(resultType);
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            // ignore
        }
    }

    private static class UnMappedColumnAutoMapping {
        private final String column;
        private final String property;
        private final TypeHandler<?> typeHandler;
        private final boolean primitive;

        public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
            this.column = column;
            this.property = property;
            this.typeHandler = typeHandler;
            this.primitive = primitive;
        }
    }
}
