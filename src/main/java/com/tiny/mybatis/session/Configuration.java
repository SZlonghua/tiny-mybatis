package com.tiny.mybatis.session;

import com.tiny.mybatis.binding.MapperRegistry;
import com.tiny.mybatis.datasource.pooled.PooledDataSourceFactory;
import com.tiny.mybatis.datasource.unpooled.UnpooledDataSourceFactory;
import com.tiny.mybatis.executor.Executor;
import com.tiny.mybatis.executor.SimpleExecutor;
import com.tiny.mybatis.executor.parameter.ParameterHandler;
import com.tiny.mybatis.executor.resultset.DefaultResultSetHandler;
import com.tiny.mybatis.executor.resultset.ResultSetHandler;
import com.tiny.mybatis.executor.statement.RoutingStatementHandler;
import com.tiny.mybatis.executor.statement.StatementHandler;
import com.tiny.mybatis.mapping.BoundSql;
import com.tiny.mybatis.mapping.Environment;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.plugin.Interceptor;
import com.tiny.mybatis.plugin.InterceptorChain;
import com.tiny.mybatis.reflection.DefaultReflectorFactory;
import com.tiny.mybatis.reflection.ReflectorFactory;
import com.tiny.mybatis.reflection.factory.DefaultObjectFactory;
import com.tiny.mybatis.reflection.factory.ObjectFactory;
import com.tiny.mybatis.scripting.LanguageDriver;
import com.tiny.mybatis.scripting.LanguageDriverRegistry;
import com.tiny.mybatis.scripting.defaults.RawLanguageDriver;
import com.tiny.mybatis.scripting.xmltags.XMLLanguageDriver;
import com.tiny.mybatis.transaction.Transaction;
import com.tiny.mybatis.transaction.jdbc.JdbcTransactionFactory;
import com.tiny.mybatis.type.TypeAliasRegistry;
import com.tiny.mybatis.type.TypeHandlerRegistry;

import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {

    protected Environment environment;

    protected Properties variables = new Properties();

    protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

    protected final Set<String> loadedResources = new HashSet<>();

    protected String databaseId;

    protected final LanguageDriverRegistry languageRegistry = new LanguageDriverRegistry();

    protected final Map<String, MappedStatement> mappedStatements = new ConcurrentHashMap<>();

    protected ExecutorType defaultExecutorType = ExecutorType.SIMPLE;


    protected final MapperRegistry mapperRegistry = new MapperRegistry(this);

    protected final InterceptorChain interceptorChain = new InterceptorChain();

    protected final TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry(this);

    protected ObjectFactory objectFactory = new DefaultObjectFactory();

    protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

    public ExecutorType getDefaultExecutorType() {
        return defaultExecutorType;
    }

    public void setDefaultExecutorType(ExecutorType defaultExecutorType) {
        this.defaultExecutorType = defaultExecutorType;
    }

    public Configuration() {
        typeAliasRegistry.registerAlias("JDBC", JdbcTransactionFactory.class);
        typeAliasRegistry.registerAlias("POOLED", PooledDataSourceFactory.class);
        typeAliasRegistry.registerAlias("UNPOOLED", UnpooledDataSourceFactory.class);

        languageRegistry.setDefaultDriverClass(XMLLanguageDriver.class);
        languageRegistry.register(RawLanguageDriver.class);
    }


    public void addInterceptor(Interceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }
    public Properties getVariables() {
        return variables;
    }

    public void setVariables(Properties variables) {
        this.variables = variables;
    }

    public TypeAliasRegistry getTypeAliasRegistry() {
        return typeAliasRegistry;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public void addLoadedResource(String resource) {
        loadedResources.add(resource);
    }

    public boolean isResourceLoaded(String resource) {
        return loadedResources.contains(resource);
    }

    public String getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(String databaseId) {
        this.databaseId = databaseId;
    }


    public LanguageDriverRegistry getLanguageRegistry() {
        return languageRegistry;
    }

    public LanguageDriver getLanguageDriver(Class<? extends LanguageDriver> langClass) {
        if (langClass == null) {
            return languageRegistry.getDefaultDriver();
        }
        languageRegistry.register(langClass);
        return languageRegistry.getDriver(langClass);
    }

    public LanguageDriver getDefaultScriptingLanguageInstance() {
        return languageRegistry.getDefaultDriver();
    }

    public void addMappedStatement(MappedStatement ms) {
        mappedStatements.put(ms.getId(), ms);
    }

    public Executor newExecutor(Transaction transaction, ExecutorType executorType) {
        Executor executor = new SimpleExecutor(this, transaction);
        executor = (Executor) interceptorChain.pluginAll(executor);
        return executor;
    }

    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        return mapperRegistry.getMapper(type, sqlSession);
    }

    public boolean hasMapper(Class<?> type) {
        return mapperRegistry.hasMapper(type);
    }

    public <T> void addMapper(Class<T> type) {
        mapperRegistry.addMapper(type);
    }

    public boolean hasStatement(String statementName) {
        return mappedStatements.containsKey(statementName);
    }

    public MappedStatement getMappedStatement(String id) {
        return mappedStatements.get(id);
    }


    public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        StatementHandler statementHandler = new RoutingStatementHandler(executor, mappedStatement, parameterObject, rowBounds, resultHandler, boundSql);
        statementHandler = (StatementHandler) interceptorChain.pluginAll(statementHandler);
        return statementHandler;
    }

    public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        ParameterHandler parameterHandler = mappedStatement.getLang().createParameterHandler(mappedStatement, parameterObject, boundSql);
        parameterHandler = (ParameterHandler) interceptorChain.pluginAll(parameterHandler);
        return parameterHandler;
    }

    public TypeHandlerRegistry getTypeHandlerRegistry() {
        return typeHandlerRegistry;
    }


    public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement, RowBounds rowBounds, ParameterHandler parameterHandler,
                                                ResultHandler resultHandler, BoundSql boundSql) {
        ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
        resultSetHandler = (ResultSetHandler) interceptorChain.pluginAll(resultSetHandler);
        return resultSetHandler;
    }

    public ObjectFactory getObjectFactory() {
        return objectFactory;
    }

    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    public ReflectorFactory getReflectorFactory() {
        return reflectorFactory;
    }

    public void setReflectorFactory(ReflectorFactory reflectorFactory) {
        this.reflectorFactory = reflectorFactory;
    }
}
