package com.tiny.mybatis.datasource.unpooled;

import com.tiny.mybatis.datasource.DataSourceFactory;

import javax.sql.DataSource;
import java.util.Properties;

public class UnpooledDataSourceFactory implements DataSourceFactory {

    protected DataSource dataSource;

    public UnpooledDataSourceFactory() {
        this.dataSource = new UnpooledDataSource();
    }
    @Override
    public void setProperties(Properties props) {
        UnpooledDataSource ds = (UnpooledDataSource)dataSource;
        ds.setDriver((String)props.get("driver"));
        ds.setUrl((String)props.get("url"));
        ds.setUsername((String)props.get("username"));
        ds.setPassword((String)props.get("password"));
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
}
