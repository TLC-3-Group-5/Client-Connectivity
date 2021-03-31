package io.turntabl.producer.resources.controller;

import io.turntabl.producer.resources.model.*;
import io.turntabl.producer.resources.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="portfolio")
public class PortfolioController {
    private  final PortfolioService portfolioService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${app.trade_engine_url}")
    private String tradeEngineUrl;

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

    @PutMapping(path="/update-balance/{portfolioId}")
    public void updateClientBalance
            (@PathVariable("portfolioId") Long portfolioId, @RequestBody Double valueOfOrder){
        portfolioService.updateClientBalance(portfolioId, valueOfOrder);
    }

    @PutMapping(path="/update-stock/{portfolioId}/{product}")
    public void updateClientStock(@PathVariable("portfolioId") Long portfolioId,
                                  @PathVariable("product") String product,
                                  @RequestBody Integer quantity){
        portfolioService.updateClientStock(portfolioId, product, quantity);
    }

    @PutMapping(path="/add-stock-to-portfolio/{portfolioId}")
    public void addStockToPortfolio(@PathVariable("portfolioId") Long portfolioId,
                                    @RequestBody TradeList trade){
        portfolioService.updatePortfolioOfOrder(portfolioId, trade);
    }

    @PutMapping(path="/update-client-balance-after-sale/{portfolioId}")
    public void updateBalanceAfterSale(@PathVariable("portfolioId") Long portfolioId,
                                       @RequestBody Double valueOfTrades){
        portfolioService.updateBalanceAfterSale(portfolioId, valueOfTrades);
    }

    // Get Trades on a specific Client order
    @GetMapping(path="order-trade/{orderId}")
    public String getTradesOnOrder(@PathVariable("orderId") Long orderId){
      return  restTemplate.getForObject(tradeEngineUrl.concat("/trade/order/").concat(String.valueOf(orderId)), String.class);
    }

    // Get Profit or Loss on a portfolio
    @GetMapping(path="profit-or-loss/{portfolioId}")
    public Map<String, Double> getProfitOrLoss(@PathVariable("portfolioId") Long portfolioId){
        return portfolioService.getProfitLoss(portfolioId);
    }

    // Cancel Order
    @DeleteMapping(path = "cancel-order/{orderId}")
    public Response cancelOrder(@PathVariable("orderId") Long orderId){
        HttpEntity<?> request = new HttpEntity<>("");
        return restTemplate.exchange(tradeEngineUrl.concat("/trade/cancel-order/").concat(String.valueOf(orderId)), HttpMethod.DELETE, request,Response.class).getBody();
    }

}
