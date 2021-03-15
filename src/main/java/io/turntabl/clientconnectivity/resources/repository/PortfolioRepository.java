package io.turntabl.clientconnectivity.resources.repository;

import io.turntabl.clientconnectivity.resources.model.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
}
