package dev.rexijie.auth.tokenservices.openid;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.util.Assert;

import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;

/*
    TODO - implement c-hash claim
 */
/**
 * An implementation of {@link TokenGranter} that grants id_tokens
 * using the implicit token grant
 * #UNUSED
 *
 * @author Rex Ijiekhuamen
 */
public class IDTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = ID_TOKEN;

    private IDTokenGranter(AuthorizationServerOidcTokenServices tokenServices,
                             ClientDetailsService clientDetailsService,
                             OAuth2RequestFactory requestFactory,
                             String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    public IDTokenGranter(AuthorizationServerOidcTokenServices tokenServices,
                          ClientDetailsService clientDetailsService,
                          OAuth2RequestFactory requestFactory) {
        this(tokenServices, clientDetailsService, requestFactory, "id_token");
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest clientToken) {

        Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
        if (userAuth==null || !userAuth.isAuthenticated()) {
            throw new InsufficientAuthenticationException("There is no currently logged in user");
        }
        Assert.state(clientToken instanceof ImplicitTokenRequest, "An ImplicitTokenRequest is required here. Caller needs to wrap the TokenRequest.");

        OAuth2Request requestForStorage = ((ImplicitTokenRequest)clientToken).getOAuth2Request();

        return new OAuth2Authentication(requestForStorage, userAuth);

    }
}
