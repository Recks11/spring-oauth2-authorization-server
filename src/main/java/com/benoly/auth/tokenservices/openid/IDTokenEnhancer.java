package com.benoly.auth.tokenservices.openid;

import com.benoly.auth.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static com.benoly.auth.constants.Claims.OpenIdClaims.*;

public class IDTokenEnhancer implements IDTokenClaimsEnhancer {

    @Override
    public Map<String, Object> enhance(Map<String, Object> originalClaims) {
        return originalClaims;
    }

    @Override
    public Map<String, Object> addProfileClaims(Map<String, Object> originalClaims, UserDetails user) {
        Map<String, Object> claims = new HashMap<>(originalClaims);
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        claims.put(NAME_CLAIM, profile.getFullName());
        claims.put(FAMILY_NAME_CLAIM, profile.getLastName());
        claims.put(GIVEN_NAME_CLAIM, profile.getFirstName());
        claims.put(PREFERRED_USERNAME_CLAIM, profile.getUsername());
        claims.put(BIRTH_DATE_CLAIM, profile.getDataOfBirth());
        return claims;
    }

    @Override
    public Map<String, Object> addEmailClaims(Map<String, Object> originalClaims, UserDetails user) {
        Map<String, Object> claims = new HashMap<>(originalClaims);
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        claims.put(EMAIL_CLAIM, profile.getEmail());
        return claims;
    }

    @Override
    public Map<String, Object> addAddressClaims(Map<String, Object> originalClaims, UserDetails user) {
        // add email and emaill_verified
        return originalClaims;
    }

    @Override
    public Map<String, Object> addPhoneClaims(Map<String, Object> originalClaims, UserDetails user) {
        // add phone numbers
        return originalClaims;
    }
}
