package dev.rexijie.auth.service;

import dev.rexijie.auth.model.client.Client;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.NoSuchClientException;

import java.util.List;

public interface ClientService extends ClientDetailsService {
    Client addClient(Client client);
    Client updateClient(String clientId, Client client);
    Client updateClientSecret(String clientId, String secret);
    void removeClientDetails(String clientId) throws NoSuchClientException;
    List<Client> listClientDetails();
}
