package io.turntabl.clientconnectivity.resources;

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

    public Response addPortfolio(Portfolio portfolio, String email){
        Response response = new Response();

        if(!email.isEmpty()){
            Client client = clientRepository.findClientByEmail(email).orElse(null);
            portfolio.setClient(client);
            portfolio.setName(portfolio.getName());
            response.setStatus("Portfolio created successfully");
            response.setCode(HttpStatus.OK.value());
        }else{
            response.setStatus("Email is required");
            response.setCode(HttpStatus.BAD_REQUEST.value());
        }
        return response;
    }
}
