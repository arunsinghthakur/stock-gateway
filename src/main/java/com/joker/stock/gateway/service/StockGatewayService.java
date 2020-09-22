package com.joker.stock.gateway.service;

import com.atlassian.braid.Braid;
import com.atlassian.braid.BraidGraphQL;
import com.atlassian.braid.Link;
import com.atlassian.braid.SchemaNamespace;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import com.google.gson.Gson;
import graphql.ExecutionInput;
import graphql.ExecutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.ArrayList;
import java.util.function.Supplier;

@Service
public class StockGatewayService {
    @Value("stock.service.namespace")
    private String stockSchemaNamespace;
    @Value("stock.service.url")
    private String stockServiceUrl;
    @Value("stock-price.service.namespace")
    private String stockPriceSchemaNamespace;
    @Value("stock-price.service.url")
    private String stockPriceServiceUrl;

    @Autowired
    private RemoteIntrospectionService remoteIntrospectionService;

    public String execute(String query) {
        Gson gson = new Gson();
        SchemaNamespace STOCK_SCHEMA_NAMESPACE = SchemaNamespace.of(stockSchemaNamespace);
        SchemaNamespace STOCK_PRICE_SCHEMA_NAMESPACE = SchemaNamespace.of(stockPriceSchemaNamespace);
        Supplier<Reader> stockServiceSchemaProvider = () -> remoteIntrospectionService.get(stockServiceUrl);
        Supplier<Reader> stockPriceSchemaProvider = () -> remoteIntrospectionService.get(stockPriceServiceUrl);
        ArrayList<Link> links = new ArrayList();
        links.add(Link.from(STOCK_SCHEMA_NAMESPACE, "stockPrice", "stockPrice").to(STOCK_PRICE_SCHEMA_NAMESPACE, "stockPrice").build());

        Braid braid = Braid
                .builder()
                .schemaSource(
                        QueryExecutorSchemaSource
                                .builder()
                                .namespace(STOCK_SCHEMA_NAMESPACE)
                                .schemaProvider(stockServiceSchemaProvider)
                                .remoteRetriever(new RemoteRetrieverService(stockServiceUrl))
                                .build())
                .schemaSource(
                        QueryExecutorSchemaSource
                                .builder()
                                .namespace(STOCK_PRICE_SCHEMA_NAMESPACE)
                                .schemaProvider(stockPriceSchemaProvider)
                                .remoteRetriever(new RemoteRetrieverService(stockPriceServiceUrl))
                                .links(links)
                                .build())
                .build();

        BraidGraphQL graphql = braid.newGraphQL();
        ExecutionResult result = graphql
                .execute(
                        ExecutionInput
                                .newExecutionInput()
                                .query(query)
                                .build())
                .join();

        return gson.toJson(result.toSpecification());
    }
}
