package com.tiny.mybatis.session.defaults;

import com.tiny.mybatis.executor.Executor;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.session.ResultHandler;
import com.tiny.mybatis.session.RowBounds;
import com.tiny.mybatis.session.SqlSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class DefaultSqlSession implements SqlSession {

    private final Configuration configuration;
    private final Executor executor;

    private final boolean autoCommit;

    public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
        this.configuration = configuration;
        this.executor = executor;
        this.autoCommit = autoCommit;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public <T> T getMapper(Class<T> type) {
        return configuration.getMapper(type, this);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public <T> T selectOne(String statement, Object parameter) {
        log.info("===============selectOne");
        List<T> list = this.selectList(statement, parameter);
        if (list.size() == 1) {
            return list.get(0);
        } else if (list.size() > 1) {
            throw new RuntimeException("Expected one result (or null) to be returned by selectOne(), but found: " + list.size());
        } else {
            return null;
        }
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter) {
        return this.selectList(statement, parameter, RowBounds.DEFAULT);
    }

    @Override
    public <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds) {
        return selectList(statement, parameter, rowBounds, Executor.NO_RESULT_HANDLER);
    }

    private <E> List<E> selectList(String statement, Object parameter, RowBounds rowBounds, ResultHandler handler) {
        try {
            MappedStatement ms = configuration.getMappedStatement(statement);
            return executor.query(ms, parameter, rowBounds, handler);
        } catch (Exception e) {
            throw new RuntimeException("Error querying database.  Cause: " + e, e);
        }
    }
}
