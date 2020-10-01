package dev.rexijie.auth.tokenservices.openid;

import dev.rexijie.auth.service.ClientService;
import dev.rexijie.auth.service.SecretGenerator;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Date;

/**
 * An implementation of {@link AuthorizationServerTokenServices} that generates IDTokens only
 * #UNUSED
 * @author Rex Ijiekhuamen
 */
public class AuthorizationServerOidcTokenServices implements AuthorizationServerTokenServices {
    private final TokenStore tokenStore;
    private final TokenEnhancer tokenEnhancer;
    private final ClientService clientDetailsService;
    private final SecretGenerator secretGenerator;

    public AuthorizationServerOidcTokenServices(TokenStore tokenStore,
                                                ClientService clientDetailsService,
                                                SecretGenerator secretGenerator,
                                                TokenEnhancer tokenEnhancer) {
        this.tokenStore = tokenStore;
        this.tokenEnhancer = tokenEnhancer;
        this.secretGenerator = secretGenerator;
        this.clientDetailsService = clientDetailsService;
    }

    @Override
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        String tokenId = getSecretGenerator().generate(16);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(tokenId);
        int validitySeconds = getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0)
            token.setExpiration(new Date(System.currentTimeMillis() + (validitySeconds * 1000L)));

        token.setScope(authentication.getOAuth2Request().getScope());
        token.setTokenType("id_token");
        // add the id_token type so the token enhancer generates the ID token

        return getTokenEnhancer().enhance(token, authentication);
    }

    @Override
    public OAuth2AccessToken refreshAccessToken(String refreshToken, TokenRequest tokenRequest) throws AuthenticationException {
        // id tokens do not have refresh tokens
        return null;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        // will return null since the tokens are not stored
        return getTokenStore().getAccessToken(authentication);
    }

    private int getAccessTokenValiditySeconds(OAuth2Request request) {
        ClientDetails client = getClientDetailsService().loadClientByClientId(request.getClientId());
        if (client != null) {
            return client.getAccessTokenValiditySeconds();
        }
        return 5 * 60;
    }

    private SecretGenerator getSecretGenerator() {
        return this.secretGenerator;
    }

    public TokenStore getTokenStore() {
        return tokenStore;
    }

    public TokenEnhancer getTokenEnhancer() {
        return tokenEnhancer;
    }

    public ClientService getClientDetailsService() {
        return clientDetailsService;
    }
}
