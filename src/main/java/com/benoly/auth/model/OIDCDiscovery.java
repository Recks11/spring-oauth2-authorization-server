package com.benoly.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

import static com.benoly.auth.constants.GrantTypes.AUTHORIZATION_CODE;
import static com.benoly.auth.constants.GrantTypes.IMPLICIT;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OIDCDiscovery {
    @JsonIgnore
    private String BASE_URI = "http://127.0.0.1:8000";
    private String issuer = "http://127.0.0.1:8000";
    private String tokenEndpoint = BASE_URI + "/oauth/token";
    private String tokenKeyEndpoint = BASE_URI + "/oauth/token_key";
    private String authorizationEndpoint = BASE_URI + "/oauth/authorize";
    private String checkTokenEndpoint = BASE_URI + "/oauth/check_token";

    private String userinfoEndpoint = BASE_URI + "/user/info";

    private Set<String> userinfoSigningAlgSupported = Set.of("RS256");
    private Set<String> idTokenSigningAlgValuesSupported = Set.of("RS256");
    private Set<String> token_endpoint_auth_signing_alg_values_supported = Set.of("RS256");

    private String jwksUri = BASE_URI + "/.well-known/jwks.json";
    private String revocationEndpoint = BASE_URI + "/oauth/revoke";
    private String profilesEndpoint = "";
    private Set<String> scopesSupported = Set.of("openid", "profile", "email", "read", "write");
    private Set<String> subjectTypesSupported = Set.of("public", "pairwise");
    private Set<String> responseTypesSupported = Set.of("code", "token");
    private Set<String> claimsSupported = Set.of("email",
            "family_name", "given_name", "name", "birthdate", "iss", "sub", "iat", "azp", "exp", "preferred_username");
    private Set<String> grantTypesSupported = Set.of(AUTHORIZATION_CODE, IMPLICIT);
    private Set<String> tokenEndpointAuthMethodsSupported = Set.of("client_secret_basic", "client_secret_post");
}
