package com.benoly.auth.model;

import lombok.*;
import org.codehaus.jackson.annotate.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@ToString(callSuper = true)
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
}
