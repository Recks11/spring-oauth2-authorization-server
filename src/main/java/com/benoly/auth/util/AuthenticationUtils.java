package com.benoly.auth.util;

import com.benoly.auth.model.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

public class AuthenticationUtils {

    public static User extractUserFromAuthentication(OAuth2Authentication auth2Authentication) {
        return (User) auth2Authentication.getPrincipal();
    }
}
