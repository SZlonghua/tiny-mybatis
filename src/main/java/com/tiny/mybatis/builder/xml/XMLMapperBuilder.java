package com.tiny.mybatis.builder.xml;

import com.tiny.mybatis.builder.BaseBuilder;
import com.tiny.mybatis.builder.MapperBuilderAssistant;
import com.tiny.mybatis.parsing.XNode;
import com.tiny.mybatis.parsing.XPathParser;
import com.tiny.mybatis.session.Configuration;
import com.tiny.mybatis.io.Resources;

import java.io.InputStream;
import java.util.List;

public class XMLMapperBuilder extends BaseBuilder {

    private final XPathParser parser;
    private final MapperBuilderAssistant builderAssistant;
    private final String resource;

    public XMLMapperBuilder(InputStream inputStream, Configuration configuration, String resource) {
        this(new XPathParser(inputStream, true, configuration.getVariables()),
                configuration, resource);
    }

    private XMLMapperBuilder(XPathParser parser, Configuration configuration, String resource) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.parser = parser;
        this.resource = resource;
    }

    public void parse() {
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(parser.evalNode("/mapper"));
            configuration.addLoadedResource(resource);
            bindMapperForNamespace();
        }

        /*parsePendingResultMaps();
        parsePendingCacheRefs();
        parsePendingStatements();*/
    }

    private void configurationElement(XNode context) {
        try {
            String namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.isEmpty()) {
                throw new RuntimeException("Mapper's namespace cannot be empty");
            }
            builderAssistant.setCurrentNamespace(namespace);
            //cacheRefElement(context.evalNode("cache-ref"));
            //cacheElement(context.evalNode("cache"));
            //parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            //resultMapElements(context.evalNodes("/mapper/resultMap"));
            //sqlElement(context.evalNodes("/mapper/sql"));
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing Mapper XML. The XML location is '" + resource + "'. Cause: " + e, e);
        }
    }

    private void buildStatementFromContext(List<XNode> list) {
        if (configuration.getDatabaseId() != null) {
            buildStatementFromContext(list, configuration.getDatabaseId());
        }
        buildStatementFromContext(list, null);
    }

    private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
        for (XNode context : list) {
            final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant, context, requiredDatabaseId);
            statementParser.parseStatementNode();
        }
    }


    private void bindMapperForNamespace() {
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace != null) {
            Class<?> boundType = null;
            try {
                boundType = Resources.classForName(namespace);
            } catch (ClassNotFoundException e) {
                // ignore, bound type is not required
            }
            if (boundType != null && !configuration.hasMapper(boundType)) {
                // Spring may not know the real resource name so we set a flag
                // to prevent loading again this resource from the mapper interface
                // look at MapperAnnotationBuilder#loadXmlResource
                configuration.addLoadedResource("namespace:" + namespace);
                configuration.addMapper(boundType);
            }
        }
    }
}
