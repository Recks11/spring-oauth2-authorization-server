package dev.rexijie.auth.model.token;

import dev.rexijie.auth.util.TokenUtils;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Data
@Document(collection = "accesstokens")
public class AccessToken {
    @Id
    private String tokenId;
    private OAuth2AccessToken token;
    private String username;
    private String clientId;
    private String authenticationId;
    private String refreshToken;
    private String authentication;

    public OAuth2Authentication getAuthentication() {
        return TokenUtils.deserializeAuthentication(this.authentication);
    }

    public void setAuthentication(@NonNull OAuth2Authentication authentication) {
        this.authentication = TokenUtils.serializeAuthentication(authentication);
    }
}
