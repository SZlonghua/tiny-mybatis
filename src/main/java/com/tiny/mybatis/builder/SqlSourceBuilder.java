package com.tiny.mybatis.builder;

import com.tiny.mybatis.mapping.ParameterMapping;
import com.tiny.mybatis.mapping.SqlSource;
import com.tiny.mybatis.parsing.GenericTokenParser;
import com.tiny.mybatis.parsing.TokenHandler;
import com.tiny.mybatis.session.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlSourceBuilder extends BaseBuilder {
    public SqlSourceBuilder(Configuration configuration) {
        super(configuration);
    }

    public SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters) {
        ParameterMappingTokenHandler handler = new ParameterMappingTokenHandler(configuration, parameterType, additionalParameters);
        GenericTokenParser parser = new GenericTokenParser("#{", "}", handler);
        String sql = parser.parse(originalSql);
        return new StaticSqlSource(configuration, sql,handler.getParameterMappings());
    }


    private static class ParameterMappingTokenHandler extends BaseBuilder implements TokenHandler {

        private List<ParameterMapping> parameterMappings = new ArrayList<>();
        private Class<?> parameterType;
        //private MetaObject metaParameters;

        public ParameterMappingTokenHandler(Configuration configuration, Class<?> parameterType, Map<String, Object> additionalParameters) {
            super(configuration);
            this.parameterType = parameterType;
            //this.metaParameters = configuration.newMetaObject(additionalParameters);
        }

        public List<ParameterMapping> getParameterMappings() {
            return parameterMappings;
        }

        @Override
        public String handleToken(String content) {
            parameterMappings.add(buildParameterMapping(content));
            return "?";
        }


        private ParameterMapping buildParameterMapping(String content) {
            String property = content;
            Class<?> propertyType;
            if (property == null || Map.class.isAssignableFrom(parameterType)) {
                propertyType = Object.class;
            } else {
                propertyType = parameterType;
            }
            ParameterMapping.Builder builder = new ParameterMapping.Builder(configuration, property, propertyType);

            return builder.build();
        }
    }
}
