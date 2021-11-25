package com.tiny.mybatis.io;

import java.io.IOException;
import java.io.InputStream;

public class Resources {

    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    public static InputStream getResourceAsStream(ClassLoader loader, String resource) {
        return getResourceAsStream(resource, getClassLoaders(loader));
    }

    public static InputStream getResourceAsStream(String resource, ClassLoader[] classLoader) {
        for (ClassLoader cl : classLoader) {
            if (null != cl) {

                // try to find the resource as passed
                InputStream returnValue = cl.getResourceAsStream(resource);

                // now, some class loaders want this leading "/", so we'll add it and try again if we didn't find the resource
                if (null == returnValue) {
                    returnValue = cl.getResourceAsStream("/" + resource);
                }

                if (null != returnValue) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    private static ClassLoader[] getClassLoaders(ClassLoader loader){
        return new ClassLoader[] {
                loader,
                Thread.currentThread().getContextClassLoader(),
                Resources.class.getClassLoader(),
                ClassLoader.getSystemClassLoader()
        };
    }

    public static Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    static Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {

        for (ClassLoader cl : classLoader) {

            if (null != cl) {

                try {

                    return Class.forName(name, true, cl);

                } catch (ClassNotFoundException e) {
                    // we'll ignore this until all classloaders fail to locate the class
                }

            }

        }

        throw new ClassNotFoundException("Cannot find class: " + name);

    }

}
