package com.tiny.mybatis.builder.xml;

import com.tiny.mybatis.builder.BaseBuilder;
import com.tiny.mybatis.builder.MapperBuilderAssistant;
import com.tiny.mybatis.mapping.SqlCommandType;
import com.tiny.mybatis.mapping.SqlSource;
import com.tiny.mybatis.mapping.StatementType;
import com.tiny.mybatis.parsing.XNode;
import com.tiny.mybatis.scripting.LanguageDriver;
import com.tiny.mybatis.session.Configuration;

import java.util.Locale;

public class XMLStatementBuilder extends BaseBuilder {

    private final MapperBuilderAssistant builderAssistant;
    private final XNode context;
    private final String requiredDatabaseId;

    public XMLStatementBuilder(Configuration configuration, MapperBuilderAssistant builderAssistant, XNode context, String databaseId) {
        super(configuration);
        this.builderAssistant = builderAssistant;
        this.context = context;
        this.requiredDatabaseId = databaseId;
    }

    public void parseStatementNode() {
        String id = context.getStringAttribute("id");
        String databaseId = context.getStringAttribute("databaseId");


        String parameterType = context.getStringAttribute("parameterType");
        Class<?> parameterTypeClass = resolveClass(parameterType);

        String lang = context.getStringAttribute("lang");
        LanguageDriver langDriver = getLanguageDriver(lang);

        String resultType = context.getStringAttribute("resultType");
        Class<?> resultTypeClass = resolveClass(resultType);

        String resultMap = context.getStringAttribute("resultMap");

        SqlSource sqlSource = langDriver.createSqlSource(configuration, context, parameterTypeClass);

        StatementType statementType = StatementType.valueOf(context.getStringAttribute("statementType", StatementType.PREPARED.toString()));

        String nodeName = context.getNode().getNodeName();
        SqlCommandType sqlCommandType = SqlCommandType.valueOf(nodeName.toUpperCase(Locale.ENGLISH));

        builderAssistant.addMappedStatement(id, sqlSource, statementType, sqlCommandType,
                 parameterTypeClass, resultMap,resultTypeClass, databaseId, langDriver);
    }

    private LanguageDriver getLanguageDriver(String lang) {
        Class<? extends LanguageDriver> langClass = null;
        if (lang != null) {
            langClass = resolveClass(lang);
        }
        return configuration.getLanguageDriver(langClass);
    }
}
