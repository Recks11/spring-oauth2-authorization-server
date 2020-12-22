package dev.rexijie.auth.repository;

import dev.rexijie.auth.model.client.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client, String> {
    Client findByClientId(String clientId);
}
