package com.tiny.mybatis.type;

import com.tiny.mybatis.io.Resources;
import com.tiny.mybatis.session.Configuration;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class UnknownTypeHandler extends BaseTypeHandler<Object> {

    private static final ObjectTypeHandler OBJECT_TYPE_HANDLER = new ObjectTypeHandler();
    private final Configuration config;
    private final Supplier<TypeHandlerRegistry> typeHandlerRegistrySupplier;

    public UnknownTypeHandler(Configuration configuration) {
        this.config = configuration;
        this.typeHandlerRegistrySupplier = configuration::getTypeHandlerRegistry;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType)
            throws SQLException {
        TypeHandler handler = resolveTypeHandler(parameter, jdbcType);
        handler.setParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
        TypeHandler<?> handler = resolveTypeHandler(rs, columnName);
        return handler.getResult(rs, columnName);
    }

    private TypeHandler<?> resolveTypeHandler(ResultSet rs, String column) {
        try {
            Map<String,Integer> columnIndexLookup;
            columnIndexLookup = new HashMap<>();
            ResultSetMetaData rsmd = rs.getMetaData();
            int count = rsmd.getColumnCount();
            //boolean useColumnLabel = config.isUseColumnLabel();
            for (int i = 1; i <= count; i++) {
                //String name = useColumnLabel ? rsmd.getColumnLabel(i) : rsmd.getColumnName(i);
                String name = rsmd.getColumnLabel(i);
                columnIndexLookup.put(name,i);
            }
            Integer columnIndex = columnIndexLookup.get(column);
            TypeHandler<?> handler = null;
            if (columnIndex != null) {
                handler = resolveTypeHandler(rsmd, columnIndex);
            }
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = OBJECT_TYPE_HANDLER;
            }
            return handler;
        } catch (SQLException e) {
            throw new RuntimeException("Error determining JDBC type for column " + column + ".  Cause: " + e, e);
        }
    }

    private TypeHandler<?> resolveTypeHandler(ResultSetMetaData rsmd, Integer columnIndex) {
        TypeHandler<?> handler = null;
        JdbcType jdbcType = safeGetJdbcTypeForColumn(rsmd, columnIndex);
        Class<?> javaType = safeGetClassForColumn(rsmd, columnIndex);
        if (javaType != null && jdbcType != null) {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(javaType, jdbcType);
        } else if (javaType != null) {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(javaType);
        } else if (jdbcType != null) {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(jdbcType);
        }
        return handler;
    }

    private JdbcType safeGetJdbcTypeForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
        try {
            return JdbcType.forCode(rsmd.getColumnType(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }

    private Class<?> safeGetClassForColumn(ResultSetMetaData rsmd, Integer columnIndex) {
        try {
            return Resources.classForName(rsmd.getColumnClassName(columnIndex));
        } catch (Exception e) {
            return null;
        }
    }

    private TypeHandler<?> resolveTypeHandler(Object parameter, JdbcType jdbcType) {
        TypeHandler<?> handler;
        if (parameter == null) {
            handler = OBJECT_TYPE_HANDLER;
        } else {
            handler = typeHandlerRegistrySupplier.get().getTypeHandler(parameter.getClass(), jdbcType);
            // check if handler is null (issue #270)
            if (handler == null || handler instanceof UnknownTypeHandler) {
                handler = OBJECT_TYPE_HANDLER;
            }
        }
        return handler;
    }
}
