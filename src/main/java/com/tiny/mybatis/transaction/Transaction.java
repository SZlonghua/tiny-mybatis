package com.tiny.mybatis.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface Transaction {

    /**
     * Retrieve inner database connection.
     * @return DataBase connection
     * @throws SQLException
     *           the SQL exception
     */
    Connection getConnection() throws SQLException;

    /**
     * Commit inner database connection.
     * @throws SQLException
     *           the SQL exception
     */
    void commit() throws SQLException;

    /**
     * Rollback inner database connection.
     * @throws SQLException
     *           the SQL exception
     */
    void rollback() throws SQLException;

    /**
     * Close inner database connection.
     * @throws SQLException
     *           the SQL exception
     */
    void close() throws SQLException;

    Integer getTimeout() throws SQLException;
}
