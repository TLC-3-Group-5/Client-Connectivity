package io.turntabl.clientconnectivity.resources.repository;

import io.turntabl.clientconnectivity.resources.model.OwnedStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedStockRepository extends JpaRepository<OwnedStock,Long> {
}
