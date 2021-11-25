package com.tiny.mybatis.session;

public interface ResultContext<T> {
    T getResultObject();

    int getResultCount();

    boolean isStopped();

    void stop();
}
