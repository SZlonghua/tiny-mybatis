package com.tiny.mybatis.builder.xml;


import com.tiny.mybatis.builder.BaseBuilder;
import com.tiny.mybatis.datasource.DataSourceFactory;
import com.tiny.mybatis.io.Resources;
import com.tiny.mybatis.mapping.Environment;
import com.tiny.mybatis.parsing.XNode;
import com.tiny.mybatis.parsing.XPathParser;
import com.tiny.mybatis.plugin.Interceptor;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

public class XMLConfigBuilder extends BaseBuilder {


    private boolean parsed;
    private final XPathParser parser;
    private String environment;




    public XMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
        this(new XPathParser(inputStream, true, props), environment, props);
    }

    private XMLConfigBuilder(XPathParser parser, String environment, Properties props) {
        super(new Configuration());
        this.configuration.setVariables(props);
        this.parsed = false;
        this.environment = environment;
        this.parser = parser;

    }


    public Configuration parse() {
        if (parsed) {
            throw new RuntimeException("Each XMLConfigBuilder can only be used once.");
        }
        parsed = true;
        parseConfiguration(parser.evalNode("/configuration"));
        return configuration;
    }

    private void parseConfiguration(XNode root) {
        try {
            // issue #117 read properties first
            /*propertiesElement(root.evalNode("properties"));
            Properties settings = settingsAsProperties(root.evalNode("settings"));
            loadCustomVfs(settings);
            loadCustomLogImpl(settings);
            typeAliasesElement(root.evalNode("typeAliases"));
            pluginElement(root.evalNode("plugin"));
            objectFactoryElement(root.evalNode("objectFactory"));
            objectWrapperFactoryElement(root.evalNode("objectWrapperFactory"));
            reflectorFactoryElement(root.evalNode("reflectorFactory"));
            settingsElement(settings);
            // read it after objectFactory and objectWrapperFactory issue #631
            environmentsElement(root.evalNode("environments"));
            databaseIdProviderElement(root.evalNode("databaseIdProvider"));
            typeHandlerElement(root.evalNode("typeHandlers"));
            mapperElement(root.evalNode("mappers"));*/


            pluginElement(root.evalNode("plugins"));
            environmentsElement(root.evalNode("environments"));
            mapperElement(root.evalNode("mappers"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing SQL Mapper Configuration. Cause: " + e, e);
        }
    }

    private void pluginElement(XNode parent) throws Exception {
        if (parent != null) {
            for (XNode child : parent.getChildren()) {
                String interceptor = child.getStringAttribute("interceptor");
                Properties properties = child.getChildrenAsProperties();
                Interceptor interceptorInstance = (Interceptor) resolveClass(interceptor).getDeclaredConstructor().newInstance();
                interceptorInstance.setProperties(properties);
                configuration.addInterceptor(interceptorInstance);
            }
        }
    }

    private void mapperElement(XNode parent) throws Exception {
        if(parent!=null){
            for (XNode mapper:parent.getChildren()){
                String resource = mapper.getStringAttribute("resource");
                try(InputStream inputStream = Resources.getResourceAsStream(resource)) {
                    XMLMapperBuilder mapperParser = new XMLMapperBuilder(inputStream, configuration, resource);
                    mapperParser.parse();
                }
            }

        }

    }

    private void environmentsElement(XNode context) throws Exception {
        if (context != null) {
            if (environment == null) {
                environment = context.getStringAttribute("default");
            }
            for (XNode child : context.getChildren()) {
                String id = child.getStringAttribute("id");
                if (isSpecifiedEnvironment(id)) {
                    TransactionFactory txFactory = transactionManagerElement(child.evalNode("transactionManager"));
                    DataSourceFactory dsFactory = dataSourceElement(child.evalNode("dataSource"));
                    DataSource dataSource = dsFactory.getDataSource();
                    Environment.Builder environmentBuilder = new Environment.Builder(id)
                            .transactionFactory(txFactory)
                            .dataSource(dataSource);
                    configuration.setEnvironment(environmentBuilder.build());
                    break;
                }
            }
        }
    }

    private DataSourceFactory dataSourceElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            DataSourceFactory factory = (DataSourceFactory) resolveClass(type).getDeclaredConstructor().newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new RuntimeException("Environment declaration requires a DataSourceFactory.");
    }

    private TransactionFactory transactionManagerElement(XNode context) throws Exception {
        if (context != null) {
            String type = context.getStringAttribute("type");
            Properties props = context.getChildrenAsProperties();
            TransactionFactory factory = (TransactionFactory) resolveClass(type).getDeclaredConstructor().newInstance();
            factory.setProperties(props);
            return factory;
        }
        throw new RuntimeException("Environment declaration requires a TransactionFactory.");
    }



    private boolean isSpecifiedEnvironment(String id) {
        if (environment == null) {
            throw new RuntimeException("No environment specified.");
        }
        if (id == null) {
            throw new RuntimeException("Environment requires an id attribute.");
        }
        return environment.equals(id);
    }




}
