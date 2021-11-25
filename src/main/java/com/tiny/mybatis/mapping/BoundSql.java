package com.tiny.mybatis.mapping;

import com.tiny.mybatis.session.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoundSql {

    private final String sql;
    private final Object parameterObject;
    private final Map<String, Object> additionalParameters;
    private final List<ParameterMapping> parameterMappings;

    public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Object parameterObject) {
        this.sql = sql;
        this.parameterObject = parameterObject;
        this.parameterMappings = parameterMappings;
        this.additionalParameters = new HashMap<>();
    }

    public String getSql() {
        return sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }
}
