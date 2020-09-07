package com.benoly.auth.tokenservices.openid;

import com.benoly.auth.tokenservices.JwtClaimsEnhancer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface IDTokenClaimsEnhancer extends JwtClaimsEnhancer {

    void addProfileClaims(Map<String, Object> originalClaims, UserDetails user);
    void addEmailClaims(Map<String, Object> originalClaims, UserDetails user);
    void addAddressClaims(Map<String, Object> originalClaims, UserDetails user);
    void addPhoneClaims(Map<String, Object> originalClaims, UserDetails user);


    default <T> T getUserFromUserDetails(UserDetails userDetails, Class<T> userClazz) {
        return userClazz.cast(userDetails);
    }
}
