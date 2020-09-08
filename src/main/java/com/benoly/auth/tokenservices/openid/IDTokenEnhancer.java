package com.benoly.auth.tokenservices.openid;

import com.benoly.auth.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static com.benoly.auth.constants.Claims.OpenIdClaims.*;

public class IDTokenEnhancer implements IDTokenClaimsEnhancer {

    @Override
    public Map<String, Object> enhance(Map<String, Object> originalClaims) {
        return originalClaims;
    }
//TODO update to use oidc userinfo
    @Override
    public void addProfileClaims(Map<String, Object> originalClaims, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        originalClaims.put(NAME_CLAIM, profile.getFullName());
        originalClaims.put(FAMILY_NAME_CLAIM, profile.getLastName());
        originalClaims.put(GIVEN_NAME_CLAIM, profile.getFirstName());
        originalClaims.put(PREFERRED_USERNAME_CLAIM, profile.getUsername());
        originalClaims.put(BIRTH_DATE_CLAIM, profile.getDataOfBirth());
    }

    @Override
    public void addEmailClaims(Map<String, Object> originalClaims, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        originalClaims.put(EMAIL_CLAIM, profile.getEmail());
        originalClaims.put(EMAIL_VERIFIED, profile.isEmailVerified());
    }

    @Override
    public void addAddressClaims(Map<String, Object> originalClaims, UserDetails user) {
        // add email and emaill_verified
    }

    @Override
    public void addPhoneClaims(Map<String, Object> originalClaims, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        originalClaims.put(PHONE_CLAIM, profile.getPhoneNumber());
        originalClaims.put(PHONE_VERIFIED, profile.isEmailVerified());
        // add phone numbers
    }
}
