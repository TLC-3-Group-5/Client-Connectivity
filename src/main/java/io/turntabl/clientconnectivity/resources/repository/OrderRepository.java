package io.turntabl.clientconnectivity.resources.repository;

import io.turntabl.clientconnectivity.resources.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Orders,Long> {
}
