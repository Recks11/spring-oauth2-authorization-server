package com.benoly.auth.tokenservices.openid;

import com.benoly.auth.model.User;
import com.benoly.auth.model.token.IDToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static com.benoly.auth.constants.Claims.OpenIdClaims.*;

/**
 * @author Rex Ijiekhuamen
 */
public class IDTokenEnhancer implements IDTokenClaimsEnhancer {

    @Override
    public Map<String, Object> enhance(Map<String, Object> originalClaims) {
        return originalClaims;
    }

    @Override
    public IDToken enhanceClaims() {
        return null;
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

//        OidcUserInfo.builder()
//                .name(profile.getFullName())
//                .familyName(profile.getLastName())
//                .givenName(profile.getFirstName())
//                .preferredUsername(profile.getUsername())
//                .birthdate(profile.getDataOfBirth().toString());
    }

    @Override
    public void addEmailClaims(Map<String, Object> originalClaims, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        originalClaims.put(EMAIL_CLAIM, profile.getEmail());
        originalClaims.put(EMAIL_VERIFIED, profile.isEmailVerified());

//        OidcUserInfo.builder()
//                .email(profile.getEmail())
//                .emailVerified(profile.isEmailVerified());
    }

    @Override
    public void addAddressClaims(Map<String, Object> originalClaims, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        // add email and emaill_verified
//        OidcUserInfo.builder()
//                .address(profile.getAddress().toString());
    }

    @Override
    public void addPhoneClaims(Map<String, Object> originalClaims, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        originalClaims.put(PHONE_CLAIM, profile.getPhoneNumber());
        originalClaims.put(PHONE_VERIFIED, profile.isEmailVerified());
        // add phone numbers
//        OidcUserInfo.builder()
//                .phoneNumber(profile.getPhoneNumber())
//                .phoneNumberVerified(profile.isPhoneNumberVerified() ? "true" : "false");
    }
}
