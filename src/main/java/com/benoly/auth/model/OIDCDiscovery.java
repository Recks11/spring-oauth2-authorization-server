package com.benoly.auth.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.benoly.auth.constants.GrantTypes.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OIDCDiscovery {
    private String issuer = "https://rexijie.dev";
    private String tokenEndpoint = "/oauth/token";
    private String tokenKeyEndpoint = "/oauth/token_key";
    private String authorizationEndpoint = "/oauth/authorize";
    private String checkTokenEndpoint = "/oauth/check_token";
    private String revocationEndpoint = "/oauth/revoke";
    private String profilesEndpoint = "";
    private Set<String> responseTypesSupported = Set.of("code", "token", "id_token");
    private Set<String> claimsSupported = Set.of("email",
            "family_name", "given_name", "name", "birthdate", "iss", "sub", "iat", "azp", "exp", "at_hash", "preferred_username");
    private Set<String> grantTypesSupported = Set.of(AUTHORIZATION_CODE, IMPLICIT, PASSWORD, CLIENT_CREDENTIALS);
    private Set<String> tokenEndpointAuthMethodsSupported = Set.of("client_secret_basic", "client_secret_post");
}
