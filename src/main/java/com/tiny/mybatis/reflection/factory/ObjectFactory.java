package com.tiny.mybatis.reflection.factory;

import java.util.List;

public interface ObjectFactory {

    <T> T create(Class<T> type);

    <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

    <T> boolean isCollection(Class<T> type);
}
