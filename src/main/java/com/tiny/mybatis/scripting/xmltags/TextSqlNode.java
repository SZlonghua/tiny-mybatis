package com.tiny.mybatis.scripting.xmltags;

import java.util.regex.Pattern;

public class TextSqlNode implements SqlNode {

    private final String text;
    private final Pattern injectionFilter;

    public TextSqlNode(String text) {
        this(text, null);
    }

    public TextSqlNode(String text, Pattern injectionFilter) {
        this.text = text;
        this.injectionFilter = injectionFilter;
    }

    public boolean isDynamic() {
        //含有${}就是动态
        return false;
    }
    @Override
    public boolean apply(DynamicContext context) {

        context.appendSql(text);
        return true;
    }
}
