package com.tiny.mybatis.executor;

import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.session.ResultHandler;
import com.tiny.mybatis.session.RowBounds;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    ResultHandler NO_RESULT_HANDLER = null;


    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler) throws SQLException;

    <E> List<E> query(MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException;
}
