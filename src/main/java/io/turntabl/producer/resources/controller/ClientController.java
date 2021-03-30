package io.turntabl.producer.resources.controller;

import io.turntabl.producer.client.SoapClient;
import io.turntabl.producer.clientOrders.OrderRequest;
import io.turntabl.producer.resources.service.ClientService;
import io.turntabl.producer.resources.model.Client;
import io.turntabl.producer.resources.model.Response;
import io.turntabl.producer.resources.service.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    private  final PortfolioService portfolioService;

    @Autowired
    private SoapClient client;

    @Autowired
    public ClientController(ClientService clientService, PortfolioService portfolioService) {
        this.clientService = clientService;
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public List<Client> getClients(){
        return this.clientService.getClients();
    }

    @PostMapping(path="/signup")
    public Response registerNewClient(@RequestBody Client client){
        return this.clientService.addNewClient(client);
    }

    @PostMapping(path="/login")
    public Response loginClient(@RequestBody Client client){
        return this.clientService.loginClient(client);
    }

    @GetMapping(path = "{clientId}")
    public Client getClientById(@PathVariable("clientId") Long clientId){
        return this.clientService.getClient(clientId);
    }

    @GetMapping(path = "/by-email/{clientEmail}")
    public Client getClientByEmail(@PathVariable("clientEmail") String clientEmail){
        return this.clientService.getClientByEmail(clientEmail);
    }

    @PostMapping("/getOrderStatus")
    public Response createValidatedOrders(@RequestBody OrderRequest request) {
        Response response = new Response();

        if(client.getOrderStatus(request).isIsOrderValid()){
            response.setName("Order is valid");
            response.setCode(HttpStatus.CREATED.value());
        }else{
            response.setName("Order is invalid");
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setData(client.getOrderStatus(request));
        }

        return response;
    }

}
