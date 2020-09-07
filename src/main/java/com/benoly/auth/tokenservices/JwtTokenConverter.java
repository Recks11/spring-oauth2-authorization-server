package com.benoly.auth.tokenservices;

import com.benoly.auth.model.token.IDToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.Claims.*;
import static org.springframework.security.oauth2.provider.token.UserAuthenticationConverter.USERNAME;

/**
 * Custom access token converter to add custom claims
 */
public class JwtTokenConverter extends DefaultAccessTokenConverter {

    private final JwtClaimsEnhancer jwtClaimsEnhancer;

    public JwtTokenConverter(JwtClaimsEnhancer jwtClaimsEnhancer) {
        this.jwtClaimsEnhancer = jwtClaimsEnhancer;
    }

    /**
     * Convert access token using the default converter and add custom claims
     * access token type was set in the token enhancer
     * @param token          OAuth2 access token to convert
     * @param authentication authentication to convert
     */
    @Override
    public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        if (token.getTokenType().equals(IDToken.TYPE))
            return ((IDToken) token).toClaimsMap();

        var superToken = super.convertAccessToken(token, authentication);
        Claims claims = new DefaultClaims(new HashMap<>(superToken));
        if (!claims.containsKey(ISSUED_AT))
            claims.setIssuedAt(new Date());

        return jwtClaimsEnhancer.enhance(claims);
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
        if (response.containsKey("azp") && !response.containsKey(CLIENT_ID))
            response.put(CLIENT_ID, response.get("azp"));
        return super.extractAuthentication(response);
    }
}
