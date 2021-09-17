package dev.rexijie.auth.config;

import dev.rexijie.auth.model.token.KeyPairHolder;
import dev.rexijie.auth.service.ClientService;
import dev.rexijie.auth.service.UserService;
import dev.rexijie.auth.tokenservices.DefaultJwtClaimEnhancer;
import dev.rexijie.auth.tokenservices.JwtClaimsEnhancer;
import dev.rexijie.auth.tokenservices.JwtTokenConverter;
import dev.rexijie.auth.tokenservices.JwtTokenEnhancer;
import dev.rexijie.auth.tokenservices.openid.IDTokenClaimsEnhancer;
import dev.rexijie.auth.tokenservices.openid.IDTokenEnhancer;
import dev.rexijie.auth.tokenservices.openid.IdTokenGeneratingTokenEnhancer;
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

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

@Configuration
@Slf4j
public class TokenServicesConfig {
    private final UserService userService;
    private final KeyPairHolder<RSAPublicKey, RSAPrivateKey> keyPairHolder;
    private final ClientService clientService;
    private final OAuth2Properties oAuth2Properties;

    public TokenServicesConfig(UserService userService,
                               KeyPairHolder<RSAPublicKey, RSAPrivateKey> keyPairHolder,
                               ClientService clientService,
                               OAuth2Properties properties) {
        this.userService = userService;
        this.keyPairHolder = keyPairHolder;
        this.clientService = clientService;
        this.oAuth2Properties = properties;
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

    /**
     * Token enhancer responsible for converting normal tokens to Jwt.
     * This is also the AccessTokenConverter
     */
    @Bean
    public JwtAccessTokenConverter tokenEnhancer() {
        var jwtTokenEnhancer = new JwtTokenEnhancer(keyPairHolder);
        jwtTokenEnhancer.setAccessTokenConverter(accessTokenConverter());
        return jwtTokenEnhancer;
    }

    /**
     * Token enhancer responsible for generating ID tokens using normal tokens.
     * This enhancer creates the ID token in the additional information field only.
     * without actually modifying the token
     */
    @Bean
    TokenEnhancer idTokenEnhancer() {
        var idTokenEnhancer = new IdTokenGeneratingTokenEnhancer(
                userService, idTokenClaimsEnhancer(), keyPairHolder, oAuth2Properties);
        idTokenEnhancer.setAccessTokenConverter(accessTokenConverter());
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
