package com.joker.stock.gateway.service;

import com.atlassian.braid.source.GraphQLRemoteRetriever;
import com.atlassian.braid.source.Query;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class RemoteRetrieverService<C> implements GraphQLRemoteRetriever<C> {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");
    private final String url;

    public RemoteRetrieverService(String url) {
        this.url = url;
    }

    @Override
    public CompletableFuture<Map<String, Object>> queryGraphQL(Query query, C c) {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> bodyMap = new HashMap() {{
            put("query", query.getQuery());
            put("variables", query.getVariables());
        }};
        String json = gson.toJson(bodyMap);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            HashMap<String, Object> jsonResult = gson.fromJson(response.body().string(), HashMap.class);
            return CompletableFuture.completedFuture(jsonResult);
        } catch (IOException error) {
            error.printStackTrace();
            return null;
        }
    }
}