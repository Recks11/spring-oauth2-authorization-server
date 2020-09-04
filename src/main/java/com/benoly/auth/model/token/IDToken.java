package com.benoly.auth.model.token;

import com.benoly.auth.model.Entity;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class IDToken extends Entity {
    @JsonProperty("iss")
    private String issuer;
    @JsonProperty("sub")
    private String subject;
    @JsonProperty("aud")
    private String audience;
    @JsonProperty("exp")
    private String expiry;
    @JsonProperty("iat")
    private int issuedAt;
    @JsonProperty("auth_time")
    private int authTime;
    private String nonce;
    @JsonProperty("azp") // client_id
    private String authorizedParty;
}
