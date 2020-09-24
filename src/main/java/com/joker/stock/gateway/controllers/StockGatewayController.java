package com.joker.stock.gateway.controllers;

import com.atlassian.braid.BraidGraphQL;
import graphql.ExecutionInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class StockGatewayController {

    @Autowired
    private BraidGraphQL graphQL;

    @PostMapping("/graphql")
    public Object graphQLPost(@RequestBody String body) throws IOException {
        return graphQL.execute(ExecutionInput.newExecutionInput().query(body).build());
    }


}
