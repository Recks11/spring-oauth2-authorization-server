package com.benoly.auth.controller;

import com.benoly.auth.model.OIDCDiscovery;
import com.nimbusds.jose.jwk.JWKSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@FrameworkEndpoint
@PropertySource("classpath:tokenclaims.properties")
public class OIDCEndpoint {

    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;
    @Autowired
    private JWKSet jwkSet;

    @RequestMapping("/openid/.well-known/openid-configuration")
    public ResponseEntity<OIDCDiscovery> openIdDiscovery() {
        OIDCDiscovery oidcDiscovery = new OIDCDiscovery();
        oidcDiscovery.setIssuer(issuer);
        return new ResponseEntity<>(oidcDiscovery, HttpStatus.OK);
    }

    @GetMapping("/openid/.well-known/jwks.json")
    @ResponseBody
    public Map<String, Object> jwkKeys() {
        return jwkSet.toJSONObject();
    }
}
