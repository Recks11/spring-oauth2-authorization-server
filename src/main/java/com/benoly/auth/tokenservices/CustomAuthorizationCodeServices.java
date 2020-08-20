package com.benoly.auth.tokenservices;

import com.benoly.auth.model.token.AuthorizationToken;
import com.benoly.auth.repository.AuthorizationTokenRepository;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;

import java.time.LocalDateTime;

import static com.benoly.auth.util.TokenUtils.generateUUID;

/**
 * Custom authorization code services to persist authorizaiton codes.
 */
public class CustomAuthorizationCodeServices implements AuthorizationCodeServices {

    private final RandomValueStringGenerator generator;
    private final AuthorizationTokenRepository authorizationTokenRepository;

    public CustomAuthorizationCodeServices(AuthorizationTokenRepository authorizationTokenRepository) {
        this.authorizationTokenRepository = authorizationTokenRepository;
        this.generator = new RandomValueStringGenerator(12);
    }

    @Override
    public String createAuthorizationCode(OAuth2Authentication authentication) {
        var token = createAuthorizationToken();
        byte[] serializedAuthentication = SerializationUtils.serialize(authentication);
        token.setAuthentication(serializedAuthentication);
        token.setUsername(authentication.getName());
        token = authorizationTokenRepository.save(token);
        return token.getCode();
    }

    @Override
    public OAuth2Authentication consumeAuthorizationCode(String code) throws InvalidGrantException {
        var tokenOptional = authorizationTokenRepository.findByCode(code);
        if (tokenOptional.isEmpty()) throwAuthorizationCode(code);

        var token = tokenOptional.get();
        if (token.isUsed()) throwAuthorizationCode(code);

        var authentication = SerializationUtils.<OAuth2Authentication>deserialize(token.getAuthentication());
        token.setUsed(true);
        token.setAuthentication(null);
        token.setUpdatedAt(LocalDateTime.now());
        authorizationTokenRepository.save(token);

        return authentication;
    }

    protected String generateCode() {
        return generator.generate();
    }

    protected AuthorizationToken createAuthorizationToken() {
        var id = generateUUID();
        var date = LocalDateTime.now();
        var token = new AuthorizationToken(null,"", "", false);
        token.setId(id);
        token.setCode(generateCode());
        token.setCreatedAt(date);
        return token;
    }

    private void throwAuthorizationCode(String code) {
        throw new InvalidGrantException("Invalid authorization code: " + code);
    }
}
