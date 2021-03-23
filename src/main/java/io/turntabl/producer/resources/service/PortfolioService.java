package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.Client;
import io.turntabl.producer.resources.model.OwnedStock;
import io.turntabl.producer.resources.model.Portfolio;
import io.turntabl.producer.resources.model.Response;
import io.turntabl.producer.resources.repository.ClientRepository;
import io.turntabl.producer.resources.repository.OwnedStockRepository;
import io.turntabl.producer.resources.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final ClientRepository clientRepository;
    private final OwnedStockRepository ownedStockRepository;

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
}
