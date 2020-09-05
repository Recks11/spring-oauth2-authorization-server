package com.benoly.auth.config;

import com.benoly.auth.service.ClientService;
import com.benoly.auth.service.UserService;
import com.benoly.auth.tokenservices.DefaultJwtClaimEnhancer;
import com.benoly.auth.tokenservices.JwtClaimsEnhancer;
import com.benoly.auth.tokenservices.JwtTokenConverter;
import com.benoly.auth.tokenservices.JwtTokenEnhancer;
import com.benoly.auth.tokenservices.openid.IDTokenClaimsEnhancer;
import com.benoly.auth.tokenservices.openid.IDTokenEnhancer;
import com.benoly.auth.tokenservices.openid.IdTokenGeneratingTokenEnhancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.security.KeyPair;
import java.util.List;

@Configuration
@Slf4j
public class TokenServicesConfig {
    private final UserService userService;
    private final KeyPair keyPair;
    private final ClientService clientService;

    public TokenServicesConfig(UserService userService,
                               KeyPair keyPair,
                               ClientService clientService) {
        this.userService = userService;
        this.keyPair = keyPair;
        this.clientService = clientService;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        var tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(tokenEnhancerChain());
        tokenServices.setAuthenticationManager(preAuthProvider());
        tokenServices.setClientDetailsService(clientService);
        return tokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(tokenEnhancer());
    }

    @Bean
    public TokenEnhancer tokenEnhancerChain() {
        var tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> tokenEnhancers =
                List.of(tokenEnhancer(), idTokenEnhancer());
        tokenEnhancerChain.setTokenEnhancers(tokenEnhancers);
        return tokenEnhancerChain;
    }

    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        var jwtTokenEnhancer = new JwtTokenEnhancer();
        jwtTokenEnhancer.setAccessTokenConverter(accessTokenConverter());
        jwtTokenEnhancer.setKeyPair(keyPair);
        return jwtTokenEnhancer;
    }

    @Bean
    TokenEnhancer idTokenEnhancer() {
        var idTokenEnhancer = new IdTokenGeneratingTokenEnhancer(userService, idTokenClaimsEnhancer());
        idTokenEnhancer.setAccessTokenConverter(accessTokenConverter());
        idTokenEnhancer.setKeyPair(keyPair);
        return idTokenEnhancer;
    }

    @Bean
    @Primary
    public AccessTokenConverter accessTokenConverter() {
        return new JwtTokenConverter(jwtClaimsEnhancer());
    }

    @Bean
    public JwtClaimsEnhancer jwtClaimsEnhancer() {
        return new DefaultJwtClaimEnhancer(userService);
    }

    @Bean
    public IDTokenClaimsEnhancer idTokenClaimsEnhancer() {
        return new IDTokenEnhancer();
    }

    private ProviderManager preAuthProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userService));
        return new ProviderManager(provider);
    }
}
