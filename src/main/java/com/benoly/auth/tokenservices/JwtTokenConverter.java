package com.benoly.auth.tokenservices;

import com.benoly.auth.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

import static com.benoly.auth.config.WebSecurityConfig.ROLE_PREFIX;
import static io.jsonwebtoken.Claims.ISSUER;
import static io.jsonwebtoken.Claims.SUBJECT;
import static org.springframework.security.oauth2.provider.token.UserAuthenticationConverter.USERNAME;

/**
 * Custom access token converter to add custom claims
 */
public class JwtTokenConverter extends DefaultAccessTokenConverter {
    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;
    private static String ROLE_CLAIM = "role";

    private final UserService userService;

    public JwtTokenConverter(UserService userService) {
        this.userService = userService;
    }

    /**
     * Convert access token using the default converter and add custom claims
     *
     * @param token          OAuth2 access token to convert
     * @param authentication authentication to convert
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        var superToken = super.convertAccessToken(token, authentication);
        var response = new HashMap<String, Object>(superToken);
        String userName = (String) response.get("user_name");
        var user = userService.findUserByUsername(userName);

        response.remove(USERNAME);
        response.put(SUBJECT, userName);
        response.put(ISSUER, issuer);
        response.put(ROLE_CLAIM, ROLE_PREFIX + user.getRole().getName());
        return response;
    }

    /**
     * Extract access token from a previously converted Token
     *
     * @param value the value of the token
     * @param map   A map of the previously converted token
     * @return Original Token
     */
    @Override
    public OAuth2AccessToken extractAccessToken(String value, Map<String, ?> map) {
        var superToken = super.extractAccessToken(value, map);
        var info = superToken.getAdditionalInformation();

        DefaultOAuth2AccessToken oAuth2AccessToken = new DefaultOAuth2AccessToken(superToken);
        oAuth2AccessToken.setAdditionalInformation(info);
        info.remove(ISSUER);

        return oAuth2AccessToken;
    }

    @Override
    public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
        var response = new HashMap<String, Object>(map);
        Object username = response.remove(SUBJECT);
        response.put(USERNAME, username);
        return super.extractAuthentication(response);
    }
}
