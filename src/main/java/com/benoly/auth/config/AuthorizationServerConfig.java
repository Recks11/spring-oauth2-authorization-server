package com.benoly.auth.config;

import com.benoly.auth.service.ClientService;
import com.benoly.auth.service.UserService;
import com.sun.el.parser.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final ClientService clientService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final AuthorizationServerTokenServices tokenServices;
    private final PasswordEncoder passwordEncoder;

    public AuthorizationServerConfig(ClientService clientService,
                                     AuthenticationConfiguration authenticationConfiguration,
                                     AuthorizationServerTokenServices tokenServices,
                                     PasswordEncoder passwordEncoder,
                                     UserService userService) throws Exception {
        this.clientService = clientService;
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
        this.tokenServices = tokenServices;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints.
                authenticationManager(authenticationManager)
                .userDetailsService(userService)
                .tokenServices(tokenServices);
    }
}
