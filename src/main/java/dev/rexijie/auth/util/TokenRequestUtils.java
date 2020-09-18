package dev.rexijie.auth.util;

import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Request;

import static dev.rexijie.auth.constants.GrantTypes.AUTHORIZATION_CODE;
import static dev.rexijie.auth.constants.GrantTypes.IMPLICIT;

public class TokenRequestUtils {

    public static boolean isImplicitRequest(OAuth2Request request) {
        return request.getGrantType().equals(IMPLICIT);
    }
    public static boolean isImplicitRequest(AuthorizationRequest request) {
        return request.getResponseTypes().contains("token");
    }

    public static boolean isAuthorizationCodeRequest(OAuth2Request request) {
        return request.getGrantType().equals(AUTHORIZATION_CODE);
    }
    public static boolean isAuthorizationCodeRequest(AuthorizationRequest request) {
        return request.getResponseTypes().contains("code");
    }

    public static boolean isIdTokenRequest(AuthorizationRequest authorizationRequest) {
        return authorizationRequest.getResponseTypes().contains("id_token");
    }
}
