package io.turntabl.producer;

import io.turntabl.producer.client.SoapClient;
import io.turntabl.producer.clientOrders.OrderRequest;
import io.turntabl.producer.clientOrders.OrderResponse;
import io.turntabl.producer.resources.model.Orders;
import io.turntabl.producer.resources.service.OrderService;
import io.turntabl.producer.resources.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@RestController
public class ClientConnectivityApplication {

	public static void main(String[] args) {

		SpringApplication.run(ClientConnectivityApplication.class, args);

	}

}
