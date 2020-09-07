package com.benoly.auth.tokenservices.openid;

import com.benoly.auth.model.User;
import com.benoly.auth.model.token.IDToken;
import com.benoly.auth.service.UserService;
import com.benoly.auth.tokenservices.JwtTokenEnhancer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.security.KeyPair;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.benoly.auth.constants.Scopes.IDTokenScopes.EMAIL;
import static com.benoly.auth.constants.Scopes.IDTokenScopes.PROFILE;
import static com.benoly.auth.constants.Scopes.ID_SCOPE;
import static io.jsonwebtoken.Claims.AUDIENCE;

public class IdTokenGeneratingTokenEnhancer extends JwtTokenEnhancer {

    private final IDTokenClaimsEnhancer enhancer;
    private final UserService userService;

    public IdTokenGeneratingTokenEnhancer(UserService userService,
                                          IDTokenClaimsEnhancer enhancer,
                                          KeyPair keyPair,
                                          Map<String, String> headers) {
        super(keyPair, headers);
        this.userService = userService;
        this.enhancer = enhancer;
    }

    // TODO - limit openid connect generation to authorization code and implicit flows only
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2Request request = authentication.getOAuth2Request();
        if (request.getScope().contains(ID_SCOPE))
            accessToken = appendIdToken(accessToken, authentication);
        return accessToken;
    }

    private OAuth2AccessToken appendIdToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2Request request = authentication.getOAuth2Request();
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;

        Claims claims = new DefaultClaims(super.decode(token.getValue()));
        claims.put("nonce", request.getRequestParameters().get("nonce"));
        claims.put("at_hash", generateAccessTokenHash(accessToken));
        claims.put(AUDIENCE, request.getClientId());

        IDToken idToken = new IDToken(claims);
        idToken.setAuthTime(claims.getIssuedAt().getTime());
        var additionalInformation = new HashMap<String, Object>();

        String username = claims.getSubject();
        User user = userService.findUserByUsername(username);

        if (request.getScope().contains(PROFILE))
            additionalInformation.putAll(enhancer.addProfileClaims(additionalInformation, user));

        if (request.getScope().contains(EMAIL))
            additionalInformation.putAll(enhancer.addEmailClaims(additionalInformation, user));

        idToken.setAdditionalInformation(additionalInformation);
        String idTokenString = super.encode(idToken, authentication);

        token.setAdditionalInformation(Map.of(IDToken.TYPE, idTokenString));
        return token;
    }

    private String generateAccessTokenHash(OAuth2AccessToken accessToken) {
        String tokenValue = accessToken.getValue().substring(0, 16);
        return Base64.getEncoder().encodeToString(tokenValue.getBytes());
    }
}
