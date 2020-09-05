package com.benoly.auth.tokenservices;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import java.net.URL;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

public class JwtTokenEnhancer extends JwtAccessTokenConverter {
    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;

    @SneakyThrows
    @Override
    public void setJwtClaimsSetVerifier(JwtClaimsSetVerifier jwtClaimsSetVerifier) {
        var issuerUrl = new URL(issuer);
        super.setJwtClaimsSetVerifier(new IssuerClaimVerifier(issuerUrl));
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
