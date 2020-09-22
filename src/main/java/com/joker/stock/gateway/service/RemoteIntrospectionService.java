package com.joker.stock.gateway.service;

import com.google.gson.Gson;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Document;
import graphql.schema.idl.SchemaPrinter;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

@Service
public class RemoteIntrospectionService {
    private static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    public Reader get(String url) {
        Gson gson = new Gson();
        OkHttpClient client = new OkHttpClient();
        Map<String, Object> bodyMap = new HashMap() {{
            put("query", introspectionQuery());
        }};
        String json = gson.toJson(bodyMap);
        RequestBody body = RequestBody.create(json, JSON_MEDIA_TYPE);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            Map<String, Object> introspectionResult = gson.fromJson(response.body().string(), HashMap.class);
            Document schema = new IntrospectionResultToSchema().createSchemaDefinition((Map<String, Object>) introspectionResult.get("data"));
            String printedSchema = new SchemaPrinter().print(schema);
            return new StringReader(printedSchema);
        } catch (IOException ex) {
            ex.printStackTrace();
            return new StringReader("");
        }
    }

    private String introspectionQuery() {
        return "query IntrospectionQuery {\n" +
                "__schema {\n" +
                "queryType { name }\n" +
                "mutationType { name }\n" +
                "subscriptionType { name }\n" +
                "types {\n" +
                "...FullType\n" +
                "}\n" +
                "directives {\n" +
                "name\n" +
                "description\n" +
                "locations\n" +
                "args {\n" +
                "...InputValue\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "fragment FullType on __Type {\n" +
                "kind\n" +
                "name\n" +
                "description\n" +
                "fields(includeDeprecated: true) {\n" +
                "name\n" +
                "description\n" +
                "args {\n" +
                "...InputValue\n" +
                "}\n" +
                "type {\n" +
                "...TypeRef\n" +
                "}\n" +
                "isDeprecated\n" +
                "deprecationReason\n" +
                "}\n" +
                "inputFields {\n" +
                "...InputValue\n" +
                "}\n" +
                "interfaces {\n" +
                "...TypeRef\n" +
                "}\n" +
                "enumValues(includeDeprecated: true) {\n" +
                "name\n" +
                "description\n" +
                "isDeprecated\n" +
                "deprecationReason\n" +
                "}\n" +
                "possibleTypes {\n" +
                "...TypeRef\n" +
                "}\n" +
                "}\n" +
                "fragment InputValue on __InputValue {\n" +
                "name\n" +
                "description\n" +
                "type { ...TypeRef }\n" +
                "defaultValue\n" +
                "}\n" +
                "fragment TypeRef on __Type {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "ofType {\n" +
                "kind\n" +
                "name\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n" +
                "}\n";
    }

}