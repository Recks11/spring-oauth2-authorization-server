package com.benoly.auth.config;

import com.benoly.auth.service.UserService;
import com.benoly.auth.tokenservices.JwtTokenConverter;
import com.benoly.auth.tokenservices.JwtTokenEnhancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;

import java.security.KeyPair;

@Configuration
@Slf4j
public class TokenServicesConfig {
    private final UserService userService;
    private final KeyPair keyPair;

    public TokenServicesConfig(UserService userService,
                               KeyPair keyPair) {
        this.userService = userService;
        this.keyPair = keyPair;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        var tokenServices = new DefaultTokenServices();
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setTokenStore(tokenStore());
        tokenServices.setTokenEnhancer(tokenEnhancer());
        tokenServices.setAuthenticationManager(preAuthProvider());
        return tokenServices;
    }

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(tokenEnhancer());
    }

    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        var jwtTokenEnhancer = new JwtTokenEnhancer();
        jwtTokenEnhancer.setAccessTokenConverter(accessTokenConverter());
        jwtTokenEnhancer.setKeyPair(keyPair);
        return jwtTokenEnhancer;
    }

    @Bean
    @Primary
    public AccessTokenConverter accessTokenConverter() {
        return new JwtTokenConverter(userService);
    }

    private ProviderManager preAuthProvider() {
        PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
        provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<>(userService));
        return new ProviderManager(provider);
    }
}
