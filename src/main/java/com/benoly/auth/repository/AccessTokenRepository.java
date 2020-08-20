package com.benoly.auth.repository;

import com.benoly.auth.model.token.AccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface AccessTokenRepository extends MongoRepository<AccessToken, String> {
    List<AccessToken> findAllByClientId(String clientId);

    List<AccessToken> findAllByClientIdAndUsername(String clientId, String username);

    Optional<AccessToken> findByTokenId(String tokenId);

    Optional<AccessToken> findByRefreshToken(String refreshToken);

    Optional<AccessToken> findByAuthenticationId(String authenticationId);
}
