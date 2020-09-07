package com.benoly.auth.model.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.time.Instant;
import java.util.*;

import static com.benoly.auth.constants.Claims.OpenIdClaims.*;
import static io.jsonwebtoken.Claims.*;
import static org.springframework.security.oauth2.common.util.OAuth2Utils.CLIENT_ID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDToken implements OAuth2AccessToken {
    public static final String TYPE = "id_token";
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
    private long issuedAt;
    @JsonProperty("auth_time")
    private long authTime;
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
        this.issuedAt = Instant.now().getEpochSecond();
        this.authTime = claims.containsKey(ISSUED_AT) ? claims.getIssuedAt().getTime() : new Date(System.currentTimeMillis()).getTime();
        if (claims.containsKey(NONCE))
            this.nonce = claims.get(NONCE, String.class);
        if (claims.containsKey(CLIENT_ID))
            this.authorizedParty = claims.get(CLIENT_ID, String.class);
        if (claims.containsKey(ACCESS_TOKEN_HASH))
            this.accessTokenHash = claims.get(ACCESS_TOKEN_HASH, String.class);
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
        return TYPE;
    }

    @Override
    public boolean isExpired() {
        return System.currentTimeMillis() > expiry.getTime();
    }

    @Override
    public Date getExpiration() {
        return this.expiry;
    }

    @Override
    public int getExpiresIn() {
        return (int) (getExpiration().getTime() - System.currentTimeMillis());
    }

    @Override
    public String getValue() {
        return null;
    }

    public Map<String, Object> toClaimsMap() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(ISSUER, this.getIssuer());
        claims.put(SUBJECT, this.getSubject());
        claims.put(AUDIENCE, this.getAudience());
        claims.put(EXPIRATION, this.getExpiration());
        claims.put(ISSUED_AT, this.getIssuedAt());
        claims.put(AUTH_TIME, this.getAuthTime());
        if (getNonce() != null)
            claims.put(NONCE, this.getNonce());
        if (getAuthorizedParty() != null)
            claims.put(AUTHORIZED_PARTY, this.getAuthorizedParty());
        claims.put(ACCESS_TOKEN_HASH, this.getAccessTokenHash());

        for (String key : this.getAdditionalInformation().keySet()) {
            claims.put(key, this.getAdditionalInformation().get(key));
        }

        return claims;
    }
}
