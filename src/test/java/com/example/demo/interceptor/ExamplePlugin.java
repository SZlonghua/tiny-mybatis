package com.example.demo.interceptor;

import com.tiny.mybatis.executor.Executor;
import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.plugin.Interceptor;
import com.tiny.mybatis.plugin.Intercepts;
import com.tiny.mybatis.plugin.Invocation;
import com.tiny.mybatis.plugin.Signature;
import com.tiny.mybatis.session.ResultHandler;
import com.tiny.mybatis.session.RowBounds;

@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class ExamplePlugin implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Object target = invocation.getTarget();
        if(Executor.class.isAssignableFrom(target.getClass())){
            Executor executor = (Executor) target;
            System.out.println(executor.toString());
            invocation.proceed();
        }
        return invocation.proceed();
    }
}
