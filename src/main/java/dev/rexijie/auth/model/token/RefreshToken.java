package dev.rexijie.auth.model.token;

import dev.rexijie.auth.util.TokenUtils;
import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Data
@Document(collection = "refreshtokens")
public class RefreshToken {
    @Id
    private String tokenId;
    private OAuth2RefreshToken token;
    private String authentication;

    public OAuth2Authentication getAuthentication() {
        return TokenUtils.deserializeAuthentication(this.authentication);
    }

    public void setAuthentication(@NonNull OAuth2Authentication authentication) {
        this.authentication = TokenUtils.serializeAuthentication(authentication);
    }
}
