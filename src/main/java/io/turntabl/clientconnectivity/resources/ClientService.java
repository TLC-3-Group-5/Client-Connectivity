package io.turntabl.clientconnectivity.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@EnableJpaAuditing
public class ClientService {
    private final ClientRepository clientRepository;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getClients(){
        List<Client> clients = clientRepository.findAll();
        for(Client client : clients){
            client.setPassword("");
        }
        return clients;
    }

    // SignUp
    public Response addNewClient(Client client) {
        String password = client.getPassword();
        Response response = new Response();
        if (password.isEmpty()) {
            throw new IllegalStateException("Invalid password.");
        }
        Optional<Client> clientByEmail = this.clientRepository.findClientByEmail(client.getEmail());
        String encodedPassword = encoder.encode(password);
        client.setPassword(encodedPassword);

        if(clientByEmail.isPresent()){
            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setStatus("Email already taken");
            throw new IllegalStateException("Email already taken");
        }
        this.clientRepository.save(client);

        response.setCode(HttpStatus.CREATED.value());
        response.setStatus("Created Successfully");
        return response;
    }

    // Retrieve a Client by Id
    public Client getClient(Long clientId) {
        boolean exists = this.clientRepository.existsById(clientId);
        if(!exists){
            throw new IllegalStateException("client with id "+ clientId + " does not exist");
        }
        return this.clientRepository.findById(clientId).orElse(null);
    }

    public Response loginClient(Client client){
        Response response = new Response();
        Client foundClient = this.clientRepository.findClientByEmail(client.getEmail()).orElse(null);
        if(foundClient!=null){

            response.setName(foundClient.getName());
            String password = foundClient.getPassword();
            if(encoder.matches(client.getPassword(), password)){
                response.setCode(HttpStatus.OK.value());
                response.setStatus("Success");
            }else{
                response.setCode(HttpStatus.BAD_REQUEST.value());
                response.setStatus("Login Failed");
            }
        }else{
            response.setCode(HttpStatus.UNAUTHORIZED.value());
            response.setStatus("Unauthorized Failed");
        }
        return response;
    }

    // Update Client details
    @Transactional
    public void updateClient(Long clientId, String name, String email) {
        Client foundClient = this.clientRepository.findById(clientId)
                .orElseThrow(()-> new IllegalStateException("client with id " + clientId + " does not exist"));

        if(name!=null &&
                name.length()>0 &&
                !Objects.equals(foundClient.getName(), name)){
            foundClient.setName(name);
        }

        if(email!=null &&
                email.length()>0 &&
                email.contains("@") &&
                !Objects.equals(foundClient.getEmail(), email)){
            Optional<Client> clientOptional = this.clientRepository.findClientByEmail(email);

            if(clientOptional.isPresent()){
                throw new IllegalStateException("email already taken");
            }
            foundClient.setEmail(email);
        }
    }

}

