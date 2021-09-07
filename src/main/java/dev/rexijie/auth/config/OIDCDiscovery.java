package dev.rexijie.auth.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@ConfigurationProperties(prefix = "oauth2.openid.discovery")
public class OIDCDiscovery {
    @JsonIgnore
    private String baseUri;
    private String issuer;
    private String tokenEndpoint;
    private String tokenKeyEndpoint;
    private String authorizationEndpoint;
    private String checkTokenEndpoint;
    private String userinfoEndpoint;
    private String introspectionEndpoint;
    private String jwksUri;
    private String revocationEndpoint;

    private Set<String> userinfoSigningAlgSupported;
    private Set<String> idTokenSigningAlgValuesSupported;
    @JsonProperty("token_endpoint_auth_signing_alg_values_supported")
    private Set<String> tokenEndpointAuthSigningAlgorithmsSupported;


    private Set<String> scopesSupported;
    private Set<String> subjectTypesSupported;
    private Set<String> responseTypesSupported;
    private Set<String> claimsSupported;
    private Set<String> grantTypesSupported;
    private Set<String> tokenEndpointAuthMethodsSupported;

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getTokenKeyEndpoint() {
        return tokenKeyEndpoint;
    }

    public void setTokenKeyEndpoint(String tokenKeyEndpoint) {
        this.tokenKeyEndpoint = tokenKeyEndpoint;
    }

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getCheckTokenEndpoint() {
        return checkTokenEndpoint;
    }

    public void setCheckTokenEndpoint(String checkTokenEndpoint) {
        this.checkTokenEndpoint = checkTokenEndpoint;
    }

    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }

    public String getIntrospectionEndpoint() {
        return introspectionEndpoint;
    }

    public void setIntrospectionEndpoint(String introspectionEndpoint) {
        this.introspectionEndpoint = introspectionEndpoint;
    }

    public String getJwksUri() {
        return jwksUri;
    }

    public void setJwksUri(String jwksUri) {
        this.jwksUri = jwksUri;
    }

    public String getRevocationEndpoint() {
        return revocationEndpoint;
    }

    public void setRevocationEndpoint(String revocationEndpoint) {
        this.revocationEndpoint = revocationEndpoint;
    }

    public Set<String> getUserinfoSigningAlgSupported() {
        return userinfoSigningAlgSupported;
    }

    public void setUserinfoSigningAlgSupported(Set<String> userinfoSigningAlgSupported) {
        this.userinfoSigningAlgSupported = userinfoSigningAlgSupported;
    }

    public Set<String> getIdTokenSigningAlgValuesSupported() {
        return idTokenSigningAlgValuesSupported;
    }

    public void setIdTokenSigningAlgValuesSupported(Set<String> idTokenSigningAlgValuesSupported) {
        this.idTokenSigningAlgValuesSupported = idTokenSigningAlgValuesSupported;
    }

    public Set<String> getTokenEndpointAuthSigningAlgorithmsSupported() {
        return tokenEndpointAuthSigningAlgorithmsSupported;
    }

    public void setTokenEndpointAuthSigningAlgorithmsSupported(Set<String> tokenEndpointAuthSigningAlgorithmsSupported) {
        this.tokenEndpointAuthSigningAlgorithmsSupported = tokenEndpointAuthSigningAlgorithmsSupported;
    }

    public Set<String> getScopesSupported() {
        return scopesSupported;
    }

    public void setScopesSupported(Set<String> scopesSupported) {
        this.scopesSupported = scopesSupported;
    }

    public Set<String> getSubjectTypesSupported() {
        return subjectTypesSupported;
    }

    public void setSubjectTypesSupported(Set<String> subjectTypesSupported) {
        this.subjectTypesSupported = subjectTypesSupported;
    }

    public Set<String> getResponseTypesSupported() {
        return responseTypesSupported;
    }

    public void setResponseTypesSupported(Set<String> responseTypesSupported) {
        this.responseTypesSupported = responseTypesSupported;
    }

    public Set<String> getClaimsSupported() {
        return claimsSupported;
    }

    public void setClaimsSupported(Set<String> claimsSupported) {
        this.claimsSupported = claimsSupported;
    }

    public Set<String> getGrantTypesSupported() {
        return grantTypesSupported;
    }

    public void setGrantTypesSupported(Set<String> grantTypesSupported) {
        this.grantTypesSupported = grantTypesSupported;
    }

    public Set<String> getTokenEndpointAuthMethodsSupported() {
        return tokenEndpointAuthMethodsSupported;
    }

    public void setTokenEndpointAuthMethodsSupported(Set<String> tokenEndpointAuthMethodsSupported) {
        this.tokenEndpointAuthMethodsSupported = tokenEndpointAuthMethodsSupported;
    }
}
