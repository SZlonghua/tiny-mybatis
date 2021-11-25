package com.tiny.mybatis.scripting.xmltags;

import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.SqlSource;
import com.tiny.mybatis.session.Configuration;

public class DynamicSqlSource implements SqlSource {

    private final Configuration configuration;
    private final SqlNode rootSqlNode;

    public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }
    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        return null;
    }
}
