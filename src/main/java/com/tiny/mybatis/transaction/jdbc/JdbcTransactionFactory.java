package com.tiny.mybatis.transaction.jdbc;

import com.tiny.mybatis.session.TransactionIsolationLevel;
import com.tiny.mybatis.transaction.Transaction;
import com.tiny.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;

public class JdbcTransactionFactory implements TransactionFactory {
    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
