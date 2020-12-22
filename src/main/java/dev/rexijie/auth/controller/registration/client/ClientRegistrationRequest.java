package dev.rexijie.auth.controller.registration.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import dev.rexijie.auth.model.authority.Authority;
import dev.rexijie.auth.model.client.Client;
import dev.rexijie.auth.model.client.ClientProfiles;
import dev.rexijie.auth.model.client.ClientTypes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientRegistrationRequest {
    private String name;
//    @JsonProperty("client_type")
    private String clientType;
//    @JsonProperty("client_profile")
    private String clientProfile;
//    @JsonProperty("logo_url")
    private String logoUri;
    private String homepage;
//    @JsonProperty("policy_url")
    private String policyUrl;
//    @JsonProperty("default_max_age")
    private int defaultMaxAge;
//    @JsonProperty("require_auth_time_claim")
    private boolean requireAuthTimeClaim;
//    @JsonProperty("resource_ids")
    private Set<String> resourceIds;
//    @JsonProperty("grant_types_supported")
    private Set<String> grantTypesSupported;
    private Set<String> redirectUris;
    private Set<String> autoApproveScopes;
    private List<Authority> authorities;
    private int tokenValiditySeconds;
    private int refreshTokenValiditySeconds;

    public Client toClient() {
        ClientTypes clientType = ClientTypes.valueOf(getClientProfile());
        ClientProfiles clientProfile = ClientProfiles.valueOf(getClientProfile());
        Client client = new Client(getName(), clientType, clientProfile);

        client.setClientUri(getHomepage());
        client.setLogoUri(getLogoUri());
        client.setClientUri(getHomepage());
        client.setPolicyUri(getPolicyUrl());
        client.setRegisteredRedirectUri(getRedirectUris());
        client.setRequireAuthTime(isRequireAuthTimeClaim());
        client.setResourceIds(getResourceIds());
        client.setAuthorizedGrantTypes(getGrantTypesSupported());
        client.setRegisteredRedirectUri(getRedirectUris());
        client.setAutoApproveScopes(getAutoApproveScopes());
        client.setAuthorities(getAuthorities());

        if (getDefaultMaxAge() > 0)
            client.setAccessTokenValiditySeconds(getDefaultMaxAge());
        else
            client.setAccessTokenValiditySeconds(getTokenValiditySeconds());
        client.setRefreshTokenValiditySeconds(getRefreshTokenValiditySeconds());

        return client;
    }
}
