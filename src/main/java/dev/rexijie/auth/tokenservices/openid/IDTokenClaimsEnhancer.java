package dev.rexijie.auth.tokenservices.openid;

import dev.rexijie.auth.model.token.IDToken;
import dev.rexijie.auth.tokenservices.JwtClaimsEnhancer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface IDTokenClaimsEnhancer extends JwtClaimsEnhancer {

    IDToken enhanceClaims();
    void addProfileClaims(Map<String, Object> originalClaims, UserDetails user);
    void addEmailClaims(Map<String, Object> originalClaims, UserDetails user);
    void addAddressClaims(Map<String, Object> originalClaims, UserDetails user);
    void addPhoneClaims(Map<String, Object> originalClaims, UserDetails user);


    default <T> T getUserFromUserDetails(UserDetails userDetails, Class<T> userClazz) {
        return userClazz.cast(userDetails);
    }
}
