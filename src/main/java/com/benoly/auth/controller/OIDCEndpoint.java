package com.benoly.auth.controller;

import com.benoly.auth.model.OIDCDiscovery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.RequestMapping;


@FrameworkEndpoint
public class OIDCEndpoint {

    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;

    @RequestMapping("/.well-known/openid-configuration")
    public ResponseEntity<OIDCDiscovery> openIdDiscovery() {
        OIDCDiscovery oidcDiscovery = new OIDCDiscovery();
        oidcDiscovery.setIssuer(issuer);
        return new ResponseEntity<>(oidcDiscovery, HttpStatus.OK);
    }
}
