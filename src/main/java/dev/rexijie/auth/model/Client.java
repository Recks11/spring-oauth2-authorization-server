package dev.rexijie.auth.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// TODO - Update Client Object
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Client extends BaseClientDetails {
    @Id
    private String id;
    private String clientName;
    private String clientType = ClientTypes.CONFIDENTIAL.getName();
    private String clientProfile = ClientProfiles.WEB.getName();
    private String logoUri;
    private String clientUri; // uri to the homepage of the client;
    private String policyUri;
//    private String jwksUri;
//    private String jwks;
    private String selectorIdentifierUri; // json file showing alternate redirect uris
    private String subjectType; // subject types supported to use for requests to this client
    private String tokenEndpointAuthMethod;
    private int defaultMaxAge; // default value for max_age claim
    private boolean requireAuthTime; // is auth time claim required?

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Client(String clientName, ClientTypes clientType,
                  ClientProfiles clientProfile) {
        this.id = UUID.randomUUID().toString();
        this.clientName = clientName;
        this.clientType = clientType == null ? ClientTypes.CONFIDENTIAL.getName() : clientType.getName();
        this.clientProfile = clientProfile == null ? ClientProfiles.WEB.getName() : clientProfile.getName();
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void addAuthority(Authority authority) {
        if (this.getAuthorities().contains(authority)) return;
        this.getAuthorities().add(authority);
    }

    // this method will be moved to a client builder or a validator class
    public void addRedirectUri(String uri) {
        if (getClientType() == null)
            throw new IllegalStateException("can not add redirect uri before specifying client type");
        if (isPublicClient()) {
            // public clients must be served via https
            if (!uri.matches("^(https)://(\\w)*(.\\w*)+(/(\\w)*)*$"))
                throw new ClientRegistrationException("Invalid redirect Uri for public client. public clients must use the https scheme");
        }
        this.getRegisteredRedirectUri().add(uri);
    }

    @Override
    public void setRegisteredRedirectUri(Set<String> registeredRedirectUris) {
        super.setRegisteredRedirectUri(new HashSet<>());
        for (String uri : registeredRedirectUris) {
            addRedirectUri(uri);
        }
    }

    public boolean isPublicClient() {
        return getClientType().equals(ClientTypes.PUBLIC.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equals(getId(), client.getId()) &&
                Objects.equals(getClientName(), client.getClientName()) &&
                Objects.equals(getClientType(), client.getClientType()) &&
                Objects.equals(getClientProfile(), client.getClientProfile()) &&
                Objects.equals(super.getClientId(), client.getClientId()) &&
                Objects.equals(super.getClientSecret(), client.getClientSecret());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public String toString() {
        return "Client {" +
                "id: '" + getId() + '\'' +
                ", name: '" + getClientName() + '\'' +
                ", type: '" + getClientType() + '\'' +
                ", clientId: '" + this.getClientId() + '\'' +
                ", clientSecret: '" + "[SECRET]" + '\'' +
                ", scope: '" + this.getScope() + '\'' +
                ", resourceIds: '" + this.getResourceIds() + '\'' +
                ", authorizedGrantTypes: '" + this.getAuthorizedGrantTypes() + '\'' +
                ", registeredRedirectUris: '" + this.getRegisteredRedirectUri() + '\'' +
                ", authorities: '" + this.getAuthorities() + '\'' +
                ", accessTokenValiditySeconds: '" + this.getAccessTokenValiditySeconds() + '\'' +
                ", refreshTokenValiditySeconds: '" + this.getRefreshTokenValiditySeconds() + '\'' +
                ", additionalInformation: '" + this.getAdditionalInformation() + '\'' +
                ", createdAt: '" + getCreatedAt() + '\'' +
                ", updatedAt: '" + getUpdatedAt() + '\'' +
                "}";
    }
}
