package io.turntabl.producer.resources.service;

import io.turntabl.producer.resources.model.OwnedStock;
import io.turntabl.producer.resources.repository.OwnedStockRepository;
import io.turntabl.producer.resources.repository.PortfolioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnedStockService {

    private final OwnedStockRepository ownedStockRepository;

    private final PortfolioRepository portfolioRepository;

    @Autowired
    public OwnedStockService(OwnedStockRepository ownedStockRepository,
                             PortfolioRepository portfolioRepository){
        this.ownedStockRepository = ownedStockRepository;
        this.portfolioRepository = portfolioRepository;
    }

}
