package com.benoly.auth.tokenservices.openid;

import com.benoly.auth.tokenservices.JwtClaimsEnhancer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public interface IDTokenClaimsEnhancer extends JwtClaimsEnhancer {

    Map<String, Object> addProfileClaims(Map<String, Object> originalClaims, UserDetails user);
    Map<String, Object> addEmailClaims(Map<String, Object> originalClaims, UserDetails user);
    Map<String, Object> addAddressClaims(Map<String, Object> originalClaims, UserDetails user);
    Map<String, Object> addPhoneClaims(Map<String, Object> originalClaims, UserDetails user);


    default <T> T getUserFromUserDetails(UserDetails userDetails, Class<T> userClazz) {
        return userClazz.cast(userDetails);
    }
}
