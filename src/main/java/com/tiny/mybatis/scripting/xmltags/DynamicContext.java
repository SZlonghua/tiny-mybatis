package com.tiny.mybatis.scripting.xmltags;

import java.util.StringJoiner;

public class DynamicContext {

    private final StringJoiner sqlBuilder = new StringJoiner(" ");

    public void appendSql(String sql) {
        sqlBuilder.add(sql);
    }

    public String getSql() {
        return sqlBuilder.toString().trim();
    }
}
