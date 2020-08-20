package com.benoly.auth.tokenservices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.HashMap;
import java.util.Map;

/**
 * Custom access token converter to add custom claims
 */
public class CustomJwtAccessTokenConverter extends DefaultAccessTokenConverter {
    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;

    private final String issuerAttribute = "iss";

    /**
     * Convert access token using the default converter and add custom claims
     *
     * @param token          OAuth2 access token to convert
     * @param authentication authentication to use while convertingn
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        var superToken = super.convertAccessToken(token, authentication);
        var response = new HashMap<String, Object>(superToken);
        response.put(issuerAttribute, issuer);
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
        info.remove(issuerAttribute);

        return oAuth2AccessToken;
    }
}
