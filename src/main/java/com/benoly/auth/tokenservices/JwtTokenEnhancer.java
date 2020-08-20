package com.benoly.auth.tokenservices;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;
import org.springframework.stereotype.Component;

import java.net.URL;

public class JwtTokenEnhancer extends JwtAccessTokenConverter {
    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;


    @SneakyThrows
    @Override
    public void setJwtClaimsSetVerifier(JwtClaimsSetVerifier jwtClaimsSetVerifier) {
        var issuerUrl = new URL(issuer);
        super.setJwtClaimsSetVerifier(new IssuerClaimVerifier(issuerUrl));
    }
}
