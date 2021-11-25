package com.tiny.mybatis.binding;

import com.tiny.mybatis.mapping.MappedStatement;
import com.tiny.mybatis.mapping.SqlCommandType;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.session.SqlSession;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapperMethod {

    private final SqlCommand command;

    private final Method method;

    public MapperMethod(Class<?> mapperInterface, Method method, Configuration config) {
        this.command = new SqlCommand(config, mapperInterface, method);
        this.method = method;
    }

    public Object execute(SqlSession sqlSession, Object[] args) {

        Object result;
        switch (command.getType()) {
            case SELECT:
                Object param = getParam(args);
                Class<?> returnType = method.getReturnType();
                if ( Collection.class.isAssignableFrom(returnType)||returnType.isArray()) {
                    result = sqlSession.selectList(command.getName(), param);
                }else {
                    result = sqlSession.selectOne(command.getName(), param);
                }
                break;
            default:
                throw new RuntimeException("Unknown execution method for: " + command.getName());
        }
        return result;
    }

    private Object getParam(Object[] args) {
        if (args==null||args.length == 0) {
            return null;
        }
        if(args.length == 1){
            return args[0];
        }
        Map<String, Object> param = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i=0;i<parameters.length; i++){
            param.put(parameters[i].getName(),args[i]);
        }
        return param;
    }


    public static class SqlCommand {

        private final String name;
        private final SqlCommandType type;

        public SqlCommand(Configuration configuration, Class<?> mapperInterface, Method method) {
            final String methodName = method.getName();
            final Class<?> declaringClass = method.getDeclaringClass();
            MappedStatement ms = resolveMappedStatement(mapperInterface, methodName, declaringClass,
                    configuration);
            name = ms.getId();
            type = ms.getSqlCommandType();
            if (type == SqlCommandType.UNKNOWN) {
                throw new RuntimeException("Unknown execution method for: " + name);
            }
        }

        public String getName() {
            return name;
        }

        public SqlCommandType getType() {
            return type;
        }

        private MappedStatement resolveMappedStatement(Class<?> mapperInterface, String methodName,
                                                       Class<?> declaringClass, Configuration configuration) {
            String statementId = mapperInterface.getName() + "." + methodName;
            if (configuration.hasStatement(statementId)) {
                return configuration.getMappedStatement(statementId);
            } else if (mapperInterface.equals(declaringClass)) {
                return null;
            }
            for (Class<?> superInterface : mapperInterface.getInterfaces()) {
                if (declaringClass.isAssignableFrom(superInterface)) {
                    MappedStatement ms = resolveMappedStatement(superInterface, methodName,
                            declaringClass, configuration);
                    if (ms != null) {
                        return ms;
                    }
                }
            }
            return null;
        }
    }





}
