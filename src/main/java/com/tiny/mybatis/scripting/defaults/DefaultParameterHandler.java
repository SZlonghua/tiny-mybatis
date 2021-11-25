package com.tiny.mybatis.scripting.defaults;

import com.tiny.mybatis.executor.parameter.ParameterHandler;
import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.mapping.ParameterMapping;
import com.tiny.mybatis.mapping.ParameterMode;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class DefaultParameterHandler implements ParameterHandler {

    //private final TypeHandlerRegistry typeHandlerRegistry;

    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        //this.typeHandlerRegistry = mappedStatement.getConfiguration().getTypeHandlerRegistry();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

        if (parameterMappings != null) {
            for (int i = 0; i < parameterMappings.size(); i++) {
                ParameterMapping parameterMapping = parameterMappings.get(i);
                if (parameterMapping.getMode() != ParameterMode.OUT) {
                    Object value;
                    String propertyName = parameterMapping.getProperty();

                    if(Map.class.isAssignableFrom(parameterObject.getClass())){
                        Map m = (Map)parameterObject;
                        value = m.get(propertyName);
                    }else {
                        value = parameterObject;
                    }
                    TypeHandler typeHandler = parameterMapping.getTypeHandler();
                    try {
                        typeHandler.setParameter(ps, i + 1, value, null);
                    } catch (RuntimeException | SQLException e) {
                        throw new RuntimeException("Could not set parameters for mapping: " + parameterMapping + ". Cause: " + e, e);
                    }
                }
            }
        }
        System.out.println(ps.toString());

    }
}
