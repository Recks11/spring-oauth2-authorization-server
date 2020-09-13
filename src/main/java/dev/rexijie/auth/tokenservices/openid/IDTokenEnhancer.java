package dev.rexijie.auth.tokenservices.openid;

import dev.rexijie.auth.constants.Claims;
import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.token.IDToken;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

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
        originalClaims.put(Claims.OpenIdClaims.NAME_CLAIM, profile.getFullName());
        originalClaims.put(Claims.OpenIdClaims.FAMILY_NAME_CLAIM, profile.getLastName());
        originalClaims.put(Claims.OpenIdClaims.GIVEN_NAME_CLAIM, profile.getFirstName());
        originalClaims.put(Claims.OpenIdClaims.PREFERRED_USERNAME_CLAIM, profile.getUsername());
        originalClaims.put(Claims.OpenIdClaims.BIRTH_DATE_CLAIM, profile.getDateOfBirth());

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
        originalClaims.put(Claims.OpenIdClaims.EMAIL_CLAIM, profile.getEmail());
        originalClaims.put(Claims.OpenIdClaims.EMAIL_VERIFIED, profile.isEmailVerified());

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
        originalClaims.put(Claims.OpenIdClaims.PHONE_CLAIM, profile.getPhoneNumber());
        originalClaims.put(Claims.OpenIdClaims.PHONE_VERIFIED, profile.isEmailVerified());
        // add phone numbers
//        OidcUserInfo.builder()
//                .phoneNumber(profile.getPhoneNumber())
//                .phoneNumberVerified(profile.isPhoneNumberVerified() ? "true" : "false");
    }
}
