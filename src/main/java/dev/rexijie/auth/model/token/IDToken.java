package dev.rexijie.auth.model.token;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.rexijie.auth.util.ObjectUtils;
import dev.rexijie.auth.util.TokenUtils;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;

import java.util.*;

import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;

@Data
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IDToken implements OAuth2AccessToken {
    public static final String TYPE = ID_TOKEN;
    private OidcIdToken token;

    public IDToken(OidcIdToken idToken) {
        this.token = idToken;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
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
        return System.currentTimeMillis() > getExpiration().getTime();
    }

    @Override
    public Date getExpiration() {
        return new Date(Objects.requireNonNull(getToken().getExpiresAt()).toEpochMilli());
    }

    @Override
    public int getExpiresIn() {
        return (int) (getExpiration().getTime() - System.currentTimeMillis());
    }

    @Override
    public String getValue() {
        return null;
    }

    public synchronized Map<String, Object> getClaims() {
        Map<String, Object> cleanedMapCopy = ObjectUtils.cleanMap(getToken().getClaims());
        return TokenUtils.toOpenIdCompliantMap(cleanedMapCopy);
    }
}
