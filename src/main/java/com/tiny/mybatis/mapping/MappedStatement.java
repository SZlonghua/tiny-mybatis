package com.tiny.mybatis.mapping;

import com.tiny.mybatis.scripting.LanguageDriver;
import com.tiny.mybatis.session.Configuration;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class MappedStatement {

    private String resource;
    private Configuration configuration;
    private String id;
    private StatementType statementType;
    private SqlSource sqlSource;
    private SqlCommandType sqlCommandType;
    private String databaseId;
    private LanguageDriver lang;
    private List<ResultMap> resultMaps;


    public BoundSql getBoundSql(Object parameterObject) {
        return sqlSource.getBoundSql(parameterObject);
    }

    public List<ResultMap> getResultMaps() {
        return resultMaps;
    }


    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlSource = sqlSource;
            mappedStatement.statementType = StatementType.PREPARED;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.lang = configuration.getDefaultScriptingLanguageInstance();
            mappedStatement.resultMaps = new ArrayList<>();
        }

        public Builder resource(String resource) {
            mappedStatement.resource = resource;
            return this;
        }

        public Builder statementType(StatementType statementType) {
            mappedStatement.statementType = statementType;
            return this;
        }

        public Builder databaseId(String databaseId) {
            mappedStatement.databaseId = databaseId;
            return this;
        }

        public Builder lang(LanguageDriver driver) {
            mappedStatement.lang = driver;
            return this;
        }

        public Builder resultMaps(List<ResultMap> resultMaps) {
            mappedStatement.resultMaps = resultMaps;
            /*for (ResultMap resultMap : resultMaps) {
                mappedStatement.hasNestedResultMaps = mappedStatement.hasNestedResultMaps || resultMap.hasNestedResultMaps();
            }*/
            return this;
        }

        public MappedStatement build() {
            assert mappedStatement.configuration != null;
            assert mappedStatement.id != null;
            assert mappedStatement.sqlSource != null;
            assert mappedStatement.lang != null;
            return mappedStatement;
        }

    }
}
