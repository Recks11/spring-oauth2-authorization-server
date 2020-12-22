package dev.rexijie.auth.util;

import dev.rexijie.auth.model.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class AuthenticationUtils {

    public static User extractUserFromAuthentication(OAuth2Authentication auth2Authentication) {
        return (User) auth2Authentication.getPrincipal();
    }
}
