package com.tiny.mybatis.session;

public interface ResultHandler<T> {

    void handleResult(ResultContext<? extends T> resultContext);
}
