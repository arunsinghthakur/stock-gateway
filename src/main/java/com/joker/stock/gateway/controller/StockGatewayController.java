package com.joker.stock.gateway.controller;

import com.joker.stock.gateway.dto.QueryParameters;
import com.joker.stock.gateway.service.StockGatewayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockGatewayController {
    @Autowired
    private StockGatewayService stockGatewayService;

    @PostMapping(path = "/graphql", consumes = "application/json", produces = "application/json")
    public String graphql(QueryParameters queryParameters) {
        return stockGatewayService.execute(queryParameters.getQuery());
    }
}