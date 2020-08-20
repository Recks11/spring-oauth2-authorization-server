package com.benoly.auth.repository;

import com.benoly.auth.model.token.AuthorizationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthorizationTokenRepository extends MongoRepository<AuthorizationToken, String> {
    public Optional<AuthorizationToken> findByCode(String id);
}
