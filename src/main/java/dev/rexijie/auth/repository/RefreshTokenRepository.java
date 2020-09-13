package dev.rexijie.auth.repository;

import dev.rexijie.auth.model.token.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenId(String tokenId);
}
