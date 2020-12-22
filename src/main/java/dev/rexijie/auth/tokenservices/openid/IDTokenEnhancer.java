package dev.rexijie.auth.tokenservices.openid;

import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.token.IDToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

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

    @Override
    public void addProfileClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();

        userInfoBuilder
                .name(profile.getFullName())
                .familyName(profile.getLastName())
                .givenName(profile.getFirstName())
                .preferredUsername(profile.getUsername())
                .birthdate(profile.getDateOfBirth().toString());
    }

    @Override
    public void addEmailClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();

        userInfoBuilder
                .email(profile.getEmail())
                .emailVerified(profile.isEmailVerified());
    }

    @Override
    public void addAddressClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();
        // add email and emaill_verified
        userInfoBuilder
                .address(profile.getAddress().toString());
    }

    @Override
    public void addPhoneClaims(OidcUserInfo.Builder userInfoBuilder, UserDetails user) {
        var profile = getUserFromUserDetails(user, User.class).getUserInfo();

        // add phone numbers
        userInfoBuilder
                .phoneNumber(profile.getPhoneNumber())
                .phoneNumberVerified(profile.isPhoneNumberVerified() ? "true" : "false");
    }
}
