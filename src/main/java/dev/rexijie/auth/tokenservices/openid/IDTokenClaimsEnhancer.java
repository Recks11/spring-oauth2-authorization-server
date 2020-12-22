package dev.rexijie.auth.tokenservices.openid;

import dev.rexijie.auth.model.token.IDToken;
import dev.rexijie.auth.tokenservices.JwtClaimsEnhancer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

public interface IDTokenClaimsEnhancer extends JwtClaimsEnhancer {

    IDToken enhanceClaims();
    void addProfileClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user);
    void addEmailClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user);
    void addAddressClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user);
    void addPhoneClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user);


    default <T extends UserDetails> T getUserFromUserDetails(UserDetails userDetails, Class<T> userClazz) {
        return userClazz.cast(userDetails);
    }
}
