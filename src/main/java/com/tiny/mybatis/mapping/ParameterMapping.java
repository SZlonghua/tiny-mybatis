package com.tiny.mybatis.mapping;

import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.type.TypeHandler;
import com.tiny.mybatis.type.TypeHandlerRegistry;

public class ParameterMapping {

    private Configuration configuration;

    private String property;
    private ParameterMode mode;
    private Class<?> javaType = Object.class;
    private TypeHandler<?> typeHandler;
    /*private JdbcType jdbcType;
    private Integer numericScale;

    private String resultMapId;
    private String jdbcTypeName;
    private String expression;*/

    private ParameterMapping() {
    }


    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public ParameterMode getMode() {
        return mode;
    }

    public void setMode(ParameterMode mode) {
        this.mode = mode;
    }

    public Class<?> getJavaType() {
        return javaType;
    }

    public void setJavaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    public TypeHandler<?> getTypeHandler() {
        return typeHandler;
    }

    public void setTypeHandler(TypeHandler<?> typeHandler) {
        this.typeHandler = typeHandler;
    }

    public static class Builder {
        private ParameterMapping parameterMapping = new ParameterMapping();



        public Builder(Configuration configuration, String property, Class<?> javaType) {
            parameterMapping.configuration = configuration;
            parameterMapping.property = property;
            parameterMapping.javaType = javaType;
            parameterMapping.mode = ParameterMode.IN;
        }



        public ParameterMapping build() {
            resolveTypeHandler();
            return parameterMapping;
        }


        private void resolveTypeHandler() {
            if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
                Configuration configuration = parameterMapping.configuration;
                TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
                parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType, null);
            }
        }

    }
}
