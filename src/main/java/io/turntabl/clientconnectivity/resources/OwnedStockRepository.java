package io.turntabl.clientconnectivity.resources;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OwnedStockRepository extends JpaRepository<OwnedStock,Long> {
}
