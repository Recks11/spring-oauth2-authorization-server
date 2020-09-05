package com.benoly.auth.tokenservices;

import com.benoly.auth.model.User;
import com.benoly.auth.model.UserInfo;
import com.benoly.auth.model.token.IDToken;
import com.benoly.auth.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import java.net.URL;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static com.benoly.auth.constants.Scopes.IDTokenScopes.EMAIL;
import static com.benoly.auth.constants.Scopes.IDTokenScopes.PROFILE;
import static com.benoly.auth.constants.Scopes.ID_SCOPE;

public class JwtTokenEnhancer extends JwtAccessTokenConverter {
    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;
    private final UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClaimPopulationDelegate delegate;

    public JwtTokenEnhancer(UserService userService) {
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public void setJwtClaimsSetVerifier(JwtClaimsSetVerifier jwtClaimsSetVerifier) {
        var issuerUrl = new URL(issuer);
        super.setJwtClaimsSetVerifier(new IssuerClaimVerifier(issuerUrl));
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2AccessToken token = super.enhance(accessToken, authentication);
        OAuth2Request request = authentication.getOAuth2Request();
//        Set<String> responseTypes = request.getResponseTypes();
//        if (request.getGrantType().equals(AUTHORIZATION_CODE)) {
        if (request.getScope().contains(ID_SCOPE))
            token = convertToIdToken(token, authentication);
//        }

        return token;
    }

    private OAuth2AccessToken convertToIdToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2Request request = authentication.getOAuth2Request();
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

        Claims claims = new DefaultClaims(super.decode(token.getValue()));
        claims.put("nonce", request.getRequestParameters().get("nonce"));
        claims.put("at_hash", generateAccessTokenHash(accessToken));
        claims.setIssuedAt(new Date());

        IDToken idToken = new IDToken(claims);
        idToken.setAdditionalInformation(new DefaultClaims());
        var additionalInformation = idToken.getAdditionalInformation();
        String username = claims.getSubject();
        User user = userService.findUserByUsername(username);

        if (request.getScope().contains(PROFILE))
            delegate.populateIdTokenClaims((Claims) additionalInformation, user);

        if (request.getScope().contains(EMAIL))
            delegate.populateIdTokenEmailClaims((Claims) additionalInformation, user);

        String idTokenString = super.encode(idToken, authentication);

        token.setAdditionalInformation(Map.of("id_token", idTokenString));
        return token;
    }

    private String generateAccessTokenHash(OAuth2AccessToken accessToken) {
        String tokenValue = accessToken.getValue().substring(0, 16);
        return Base64.getEncoder().encodeToString(tokenValue.getBytes());
    }

    @Override
    public void setKeyPair(KeyPair keyPair) {
        super.setKeyPair(keyPair);
        this.setVerifierKey(generateRSAString(keyPair));
    }

    protected String generateRSAString(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return "-----BEGIN PUBLIC KEY-----"
                + Base64.getEncoder().encodeToString(publicKey.getEncoded())
                + "-----END PUBLIC KEY-----";
    }
}
