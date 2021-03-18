package io.turntabl.producer.resources.controller;

import io.turntabl.producer.resources.model.Client;
import io.turntabl.producer.resources.model.OwnedStockList;
import io.turntabl.producer.resources.service.PortfolioService;
import io.turntabl.producer.resources.model.Portfolio;
import io.turntabl.producer.resources.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="portfolio")
public class PortfolioController {
    private  final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService){
        this.portfolioService = portfolioService;
    }

    @PostMapping(path="/create")
    public Response createPortfolio(@RequestBody Portfolio portfolio){
        return this.portfolioService.addPortfolio(portfolio);
    }

    @GetMapping
    public List<Portfolio> getAllPortfolio(){ return portfolioService.getAllPortfolio();}

    @GetMapping(path = "/client-balance/{portfolioId}")
    public Double getClientBalance(@PathVariable("portfolioId") Long portfolioId){
        return portfolioService.getClientBalance(portfolioId);
    }

    @GetMapping(path = "/client-stocks/{portfolioId}")
    public OwnedStockList getClientStocks(@PathVariable("portfolioId") Long portfolioId){
        OwnedStockList ownedStockList = new OwnedStockList();
        ownedStockList.setOwnedStockList(portfolioService.getStocksOnPortfolio(portfolioId));
        return ownedStockList;
    }

}
