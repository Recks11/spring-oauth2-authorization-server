package com.benoly.auth.repository;

import com.benoly.auth.model.Client;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ClientRepository extends MongoRepository<Client, String> {
    Client findByClientId(String clientId);
}
