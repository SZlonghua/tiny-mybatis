package com.tiny.mybatis.session;

import java.io.Closeable;
import java.util.List;

public interface SqlSession extends Closeable {

    <T> T getMapper(Class<T> type);

    Configuration getConfiguration();


    <T> T selectOne(String statement, Object parameter);

    <E> List<E> selectList(String statement, Object parameter);


    <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds);
}
