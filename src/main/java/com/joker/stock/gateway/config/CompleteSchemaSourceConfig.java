package com.joker.stock.gateway.config;

import com.joker.stock.gateway.components.RemoteRetriever;
import com.atlassian.braid.SchemaSource;
import com.atlassian.braid.java.util.BraidMaps;
import com.atlassian.braid.java.util.BraidObjects;
import com.atlassian.braid.source.QueryExecutorSchemaSource;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.joker.stock.gateway.utils.ResourceUtils.getResourceAsReader;
import static com.joker.stock.gateway.utils.ResourceUtils.loadYamlMap;
import static com.atlassian.braid.source.yaml.YamlRemoteSchemaSourceBuilder.*;
import static java.util.Collections.emptyList;

@Configuration
@Data
public class CompleteSchemaSourceConfig {


    @Autowired
    private WebClient.Builder webClientConfigBuilder;

    @Bean
    public List<SchemaSource> schemaSourceList() {
        Map<String, Object> configMap = loadYamlMap(getResourceAsReader("braidFullConfig.yml"));
        return BraidMaps.get(configMap, "schemaSources")
                .map(BraidObjects::<List<Map<String, Object>>>cast)
                .map(sources -> sources.stream()
                        .map(m -> QueryExecutorSchemaSource.builder()
                                .namespace(buildSchemaNamespace(m))
                                .schemaLoader(buildSchemaLoader(m))
                                .remoteRetriever(getRemoteRetriever(m.get("url").toString()))
                                .links(buildLinks(m))
                                .extensions(buildExtensions(m))
                                .queryFieldRenames(buildQueryFieldRenames(m))
                                .mutationFieldRenames(buildMutationAliases(m))
                                .typeRenames(buildTypeRenames(m))
                                .documentMapperFactory(buildDocumentMapperFactory(m))
                                .build())
                        .collect(Collectors.<SchemaSource>toList()))
                .orElse(emptyList());
    }

    private RemoteRetriever getRemoteRetriever(String url) {
        return new RemoteRetriever(url, webClientConfigBuilder);
    }

}

