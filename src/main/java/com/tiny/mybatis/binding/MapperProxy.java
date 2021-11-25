package com.tiny.mybatis.binding;

import com.tiny.mybatis.session.SqlSession;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class MapperProxy<T> implements InvocationHandler, Serializable {

    private final SqlSession sqlSession;
    private final Class<T> mapperInterface;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return new MapperMethod(mapperInterface, method, sqlSession.getConfiguration()).execute(sqlSession, args);
    }


    public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface) {
        this.sqlSession = sqlSession;
        this.mapperInterface = mapperInterface;
    }
}
