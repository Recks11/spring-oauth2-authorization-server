package dev.rexijie.auth.controller;

import com.nimbusds.jose.jwk.JWKSet;
import dev.rexijie.auth.config.OAuth2Properties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@CrossOrigin(origins = "*", allowCredentials = "", allowedHeaders = "*")
@FrameworkEndpoint
public class OIDCEndpoint {
    private final JWKSet jwkSet;
    private final OAuth2Properties oidcDiscovery;

    public OIDCEndpoint(JWKSet jwkSet,
                        OAuth2Properties oidcDiscovery) {
        this.jwkSet = jwkSet;
        this.oidcDiscovery = oidcDiscovery;
    }

    @RequestMapping("/openid/.well-known/openid-configuration")
    public ResponseEntity<OAuth2Properties.OidcProperties> openIdDiscovery() {
        return new ResponseEntity<>(oidcDiscovery.getOpenid(), HttpStatus.OK);
    }

    @GetMapping("/openid/.well-known/jwks.json")
    @ResponseBody
    public Map<String, Object> jwkKeys() {
        return jwkSet.toJSONObject();
    }
}
