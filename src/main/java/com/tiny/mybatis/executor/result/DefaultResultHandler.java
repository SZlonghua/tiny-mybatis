package com.tiny.mybatis.executor.result;

import com.tiny.mybatis.reflection.factory.ObjectFactory;
import com.tiny.mybatis.session.ResultContext;
import com.tiny.mybatis.session.ResultHandler;

import java.util.ArrayList;
import java.util.List;

public class DefaultResultHandler implements ResultHandler<Object> {

    private final List<Object> list;

    public DefaultResultHandler() {
        list = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public DefaultResultHandler(ObjectFactory objectFactory) {
        list = objectFactory.create(List.class);
    }

    public List<Object> getResultList() {
        return list;
    }

    @Override
    public void handleResult(ResultContext<?> context) {
        list.add(context.getResultObject());
    }
}
