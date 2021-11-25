package com.tiny.mybatis.datasource.pooled;

import java.util.ArrayList;
import java.util.List;

public class PoolState {

    protected PooledDataSource dataSource;

    protected final List<PooledConnection> idleConnections = new ArrayList<>();
    protected final List<PooledConnection> activeConnections = new ArrayList<>();

    public PoolState(PooledDataSource dataSource) {
        this.dataSource = dataSource;
    }
}
