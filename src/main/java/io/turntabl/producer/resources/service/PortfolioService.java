package io.turntabl.producer.resources.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.turntabl.producer.resources.model.*;
import io.turntabl.producer.resources.model.TradeList;
import io.turntabl.producer.resources.model.ExchangeMarketData;
import io.turntabl.producer.resources.repository.ClientRepository;
import io.turntabl.producer.resources.repository.OwnedStockRepository;
import io.turntabl.producer.resources.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final ClientRepository clientRepository;
    private final OwnedStockRepository ownedStockRepository;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


    @Value("${app.MARKET_DATA_EXCHANGE_1}")
    private String exchangeOneMarketData;

    @Value("${app.MARKET_DATA_EXCHANGE_2}")
    private String exchangeTwoMarketData;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository,
                            ClientRepository clientRepository,
                            OwnedStockRepository ownedStockRepository) {
        this.portfolioRepository = portfolioRepository;
        this.clientRepository= clientRepository;
        this.ownedStockRepository = ownedStockRepository;
    }

    // Create a portfolio
    public Response addPortfolio(Portfolio portfolio){
        Response response = new Response();

        if(!portfolio.getEmail().isEmpty()){
            Client client = clientRepository.findClientByEmail(portfolio.getEmail()).orElse(null);
            portfolio.setClient(client);
            portfolio.setName(portfolio.getName());
            response.setStatus("Portfolio created successfully");
            response.setCode(HttpStatus.OK.value());
            this.portfolioRepository.save(portfolio);
        }else{
            response.setStatus("User is not found");
            response.setCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }

    // Get All Portfolios
    public List<Portfolio> getAllPortfolio(){return portfolioRepository.findAll();}

    // Get Client's Balance
    public Double getClientBalance(Long portfolioId){
        Portfolio portfolio = this.portfolioRepository.findById(portfolioId).orElse(null);
        return portfolio != null ? portfolio.getClient().getBalance() : null;
    }

    // Update Client's Balance
    public void updateClientBalance(Long portfolioId, Double valueOfOrder){
        Portfolio portfolio = this.portfolioRepository.findById(portfolioId).orElse(null);

        if(portfolio!=null){
            Client client = portfolio.getClient();
            Double Oldbalance = client.getBalance();
            client.setBalance(Oldbalance-valueOfOrder);
            clientRepository.save(client);
        }
    }

    // Get Client's Stocks on a particular portfolio
    public List<OwnedStock> getStocksOnPortfolio(Long portfolioId){
        Portfolio portfolio = portfolioRepository.findById(portfolioId).orElse(null);
        return portfolio != null ? portfolio.getOwnedStocks() : null;
    }

    // Get Portfolio
    public Portfolio getPortfolio(Long portfolioId){
        return portfolioRepository.findById(portfolioId).orElse(null);
    }

    // Update OwnedStock
    public void updateClientStock(Long portfolioId, String product, Integer quantity){
        List<OwnedStock> ownedStocks = getStocksOnPortfolio(portfolioId);
        OwnedStock stock = ownedStocks.stream()
                .filter(stock1 ->stock1.getTicker().equals(product))
                .findFirst().orElse(null);

        if(stock!=null){
            Integer oldQuantity = stock.getQuantity();
            stock.setQuantity(oldQuantity-quantity);
            ownedStockRepository.save(stock);
        }
    }

    // Add Purchased Stocks to Portfolio
    public void updatePortfolioOfOrder(Long id, TradeList trade){
        List<OwnedStock> ownedStocks = getStocksOnPortfolio(id);
        Trade oneTrade = trade.getTradeList().stream().findFirst().orElse(null);
        if(oneTrade!=null){
            OwnedStock stock = ownedStocks.stream()
                    .filter(ownedStock -> ownedStock.getTicker().equals(oneTrade.getProduct()))
                    .findFirst().orElse(null);

            Double totalValueOfTrade = trade.getTradeList().stream()
                    .mapToDouble(trade1->trade1.getQuantity()*trade1.getPrice()).sum();

            Double totalValueOfOrder = oneTrade.getOrders().getPrice() * oneTrade.getOrders().getQuantity();

            int totalQuantity = trade.getTradeList().stream().mapToInt(Trade::getQuantity).sum();
            Double averagePrice = trade.getTradeList().stream().mapToDouble(Trade::getPrice).average().orElse(0);

            if(stock==null){
                OwnedStock stock1 = new OwnedStock();
                stock1.setQuantity(totalQuantity);
                stock1.setTicker(oneTrade.getProduct());
                stock1.setPrice(averagePrice);
                stock1.setPortfolio(getPortfolio(id));
                ownedStockRepository.save(stock1);
            }else{
                int oldQuantity = stock.getQuantity();
                stock.setQuantity(oldQuantity + totalQuantity);
                ownedStockRepository.save(stock);
            }

            if(totalValueOfOrder-totalValueOfTrade>0){
                Portfolio portfolio = this.portfolioRepository.findById(id).orElse(null);
                if(portfolio!=null){
                    Client client = portfolio.getClient();
                    Double oldBalance = client.getBalance();
                    client.setBalance(oldBalance + (totalValueOfOrder-totalValueOfTrade));
                    clientRepository.save(client);
                }
            }

        }
    }

    // Update Balance After Stock Sales
    public void updateBalanceAfterSale(Long id, Double valueOfTrades){
        Portfolio portfolio = this.portfolioRepository.findById(id).orElse(null);

        if(portfolio!=null){
            Client client = portfolio.getClient();
            Double oldBalance = client.getBalance();
            client.setBalance(oldBalance + valueOfTrades);
            clientRepository.save(client);
        }
    }

    public Map<String, Double> getProfitLoss(Long id){
        List<OwnedStock> ownedStocks = getStocksOnPortfolio(id);
        Map<String, Double> profits = new HashMap<>();
        if(ownedStocks!=null){
            profits =  ownedStocks.stream().collect(Collectors.toMap(OwnedStock::getTicker,ownedStock -> {
                try {
                    ExchangeMarketData exchangeMarketData = objectMapper
                            .readValue(restTemplate.getForObject(exchangeOneMarketData.concat(ownedStock.getTicker()), String.class),
                                    ExchangeMarketData.class);
                    ExchangeMarketData exchangeMarketData_2 = objectMapper
                            .readValue(restTemplate.getForObject(exchangeTwoMarketData.concat(ownedStock.getTicker()), String.class),
                                    ExchangeMarketData.class);
                    Double tradedPrice = (exchangeMarketData.getLAST_TRADED_PRICE() + exchangeMarketData_2.getLAST_TRADED_PRICE())/2;
                    return (((tradedPrice - ownedStock.getPrice())/ownedStock.getPrice())*100);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                return Double.parseDouble(null);
            } ));
        }else{
            return profits;
        }
        return profits;
    }
}
