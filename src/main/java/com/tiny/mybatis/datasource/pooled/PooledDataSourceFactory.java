package com.tiny.mybatis.datasource.pooled;


import com.tiny.mybatis.datasource.unpooled.UnpooledDataSource;
import com.tiny.mybatis.datasource.unpooled.UnpooledDataSourceFactory;

import java.util.Properties;

public class PooledDataSourceFactory extends UnpooledDataSourceFactory {

    public PooledDataSourceFactory() {
        this.dataSource = new PooledDataSource();
    }

    @Override
    public void setProperties(Properties props) {
        UnpooledDataSource ds = ((PooledDataSource)dataSource).getDataSource();
        ds.setDriver((String)props.get("driver"));
        ds.setUrl((String)props.get("url"));
        ds.setUsername((String)props.get("username"));
        ds.setPassword((String)props.get("password"));
    }
}
