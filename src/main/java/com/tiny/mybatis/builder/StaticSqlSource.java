package com.tiny.mybatis.builder;

import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.ParameterMapping;
import com.tiny.mybatis.mapping.SqlSource;
import com.tiny.mybatis.session.Configuration;

import java.util.List;

public class StaticSqlSource implements SqlSource {

    private final String sql;
    private final Configuration configuration;

    private final List<ParameterMapping> parameterMappings;

    public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
        this.configuration = configuration;
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return new BoundSql(configuration, sql,parameterMappings, parameterObject);
    }
}
