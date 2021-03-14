package io.turntabl.clientconnectivity.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="client")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
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

}
