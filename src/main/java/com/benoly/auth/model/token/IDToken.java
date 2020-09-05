package com.benoly.auth.model.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.util.*;

import static io.jsonwebtoken.Claims.ISSUED_AT;
import static org.springframework.security.oauth2.common.util.OAuth2Utils.CLIENT_ID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDToken implements OAuth2AccessToken {
    @JsonIgnore
    private String value;
    @JsonProperty("iss")
    private String issuer;
    @JsonProperty("sub")
    private String subject;
    @JsonProperty("aud")
    private String audience;
    @JsonProperty("exp")
    private Date expiry;
    @JsonProperty("iat")
    private Date issuedAt;
    @JsonProperty("auth_time")
    private int authTime;
    private String nonce;
    @JsonProperty("azp") // client_id
    private String authorizedParty;
    @JsonProperty("at_hash") // Base64 encoded first 128 bits of the supporting access token
    private String accessTokenHash;
    @JsonIgnore
    private Map<String, Object> additionalInformation;

    public IDToken(Claims claims) {
        this.issuer = claims.getIssuer();
        this.subject = claims.getSubject();
        this.audience = claims.getAudience();
        this.expiry = claims.getExpiration();
        this.issuedAt = claims.containsKey(ISSUED_AT) ? claims.getIssuedAt() : new Date();
        if (claims.containsKey("auth_time"))
            this.authTime = claims.get("auth_time", Integer.class);
        if (claims.containsKey("nonce"))
            this.nonce = claims.get("nonce", String.class);
        if (claims.containsKey(CLIENT_ID))
            this.authorizedParty = claims.get(CLIENT_ID, String.class);
        if (claims.containsKey("at_hash"))
            this.accessTokenHash = claims.get("at_hash", String.class);
        this.additionalInformation = new HashMap<>();
    }

    @Override
    public Set<String> getScope() {
        return Collections.emptySet();
    }

    @Override
    public OAuth2RefreshToken getRefreshToken() {
        return null;
    }

    @Override
    public String getTokenType() {
        return "id_token";
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public Date getExpiration() {
        return this.expiry;
    }

    @Override
    public int getExpiresIn() {
        return 0;
    }

    @Override
    public String getValue() {
        return null;
    }
}
