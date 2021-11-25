package com.tiny.mybatis.scripting;

import com.tiny.mybatis.executor.parameter.ParameterHandler;
import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.mapping.SqlSource;
import com.tiny.mybatis.parsing.XNode;
import com.tiny.mybatis.session.Configuration;

public interface LanguageDriver {

    SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType);


    SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType);


    ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql);
}
