package io.turntabl.clientconnectivity.resources.service;

import io.turntabl.clientconnectivity.resources.model.Client;
import io.turntabl.clientconnectivity.resources.model.Portfolio;
import io.turntabl.clientconnectivity.resources.model.Response;
import io.turntabl.clientconnectivity.resources.repository.ClientRepository;
import io.turntabl.clientconnectivity.resources.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final ClientRepository clientRepository;

    @Autowired
    public PortfolioService(PortfolioRepository portfolioRepository, ClientRepository clientRepository) {
        this.portfolioRepository = portfolioRepository;
        this.clientRepository= clientRepository;
    }

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
}
