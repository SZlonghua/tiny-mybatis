package com.tiny.mybatis.scripting.xmltags;

import com.tiny.mybatis.executor.parameter.ParameterHandler;
import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.mapping.SqlSource;
import com.tiny.mybatis.parsing.XNode;
import com.tiny.mybatis.scripting.LanguageDriver;
import com.tiny.mybatis.scripting.defaults.DefaultParameterHandler;
import com.tiny.mybatis.session.Configuration;

public class XMLLanguageDriver implements LanguageDriver {
    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
        return builder.parseScriptNode();

    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        return null;
    }

    @Override
    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
    }
}
