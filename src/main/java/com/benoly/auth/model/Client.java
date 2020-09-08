package com.benoly.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class Client extends BaseClientDetails {
    @Id
    private String id;
    private String name;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Client client = (Client) o;
        return Objects.equals(getId(), client.getId()) &&
                Objects.equals(getName(), client.getName()) &&
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
                ", name: '" + getName() + '\'' +
                " clientId: '" + this.getClientId() + '\'' +
                ", clientSecret: '" + "[SECRET]" + '\'' +
                ", scope: '" + this.getScope() + '\'' +
                ", resourceIds: '" + this.getResourceIds() + '\'' +
                ", authorizedGrantTypes: '" + this.getAuthorizedGrantTypes() + '\'' +
                ", registeredRedirectUris: '" + this.getRegisteredRedirectUri() + '\'' +
                ", authorities: '" + this.getAuthorities() + '\'' +
                ", accessTokenValiditySeconds: '" + this.getAccessTokenValiditySeconds() + '\'' +
                ", refreshTokenValiditySeconds: '" + this.getRefreshTokenValiditySeconds() + '\'' +
                ", additionalInformation: '" + this.getAdditionalInformation() +'\'' +
                ", createdAt: '" + getCreatedAt() + '\'' +
                ", updatedAt: '" + getUpdatedAt() + '\'' +
                "}";
    }
}
