package com.tiny.mybatis.mapping;

public interface SqlSource {

    BoundSql getBoundSql(Object parameterObject);
}
