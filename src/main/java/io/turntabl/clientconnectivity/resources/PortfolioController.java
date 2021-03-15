package io.turntabl.clientconnectivity.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
