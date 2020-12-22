package dev.rexijie.auth.config;

import dev.rexijie.auth.config.interceptors.SessionInvalidatingHandlerInterceptor;
import dev.rexijie.auth.service.ClientService;
import dev.rexijie.auth.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenGranter;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeTokenGranter;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenGranter;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final UserService userService;
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthorizationServerTokenServices tokenServices;
    private final AuthorizationCodeServices authorizationCodeServices;
    private final AccessTokenConverter accessTokenConverter;

    public AuthorizationServerConfig(UserService userService,
                                     ClientService clientService,
                                     PasswordEncoder passwordEncoder,
                                     AuthenticationConfiguration authenticationConfiguration,
                                     AuthorizationServerTokenServices tokenServices,
                                     AuthorizationCodeServices authorizationCodeServices,
                                     AccessTokenConverter accessTokenConverter) throws Exception {
        this.userService = userService;
        this.tokenServices = tokenServices;
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
        this.authorizationCodeServices = authorizationCodeServices;
        this.accessTokenConverter = accessTokenConverter;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_CLIENT')")
                .checkTokenAccess("hasAuthority('ROLE_CLIENT')")
                .passwordEncoder(passwordEncoder);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();
        corsConfig.setMaxAge(Duration.ofMinutes(10L));

        source.registerCorsConfiguration("/oauth2/token", corsConfig);
        CorsFilter filter = new CorsFilter(source);

        security.addTokenEndpointAuthenticationFilter(filter);

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
                .accessTokenConverter(accessTokenConverter)
                .authorizationCodeServices(authorizationCodeServices)
                .tokenServices(tokenServices)
                .requestFactory(oAuth2RequestFactory())
                .tokenGranter(tokenGranter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS);

        endpoints.addInterceptor(new SessionInvalidatingHandlerInterceptor());

        // workaround to replace the authorize endpoint.
        // rename all oauth mappins and deny access to /oauth/**
        endpoints
                .pathMapping("/oauth/check_token", "/oauth2/check_token")
                .pathMapping("/oauth/token_key", "/oauth2/token_key")
                .pathMapping("/oauth/token", "/oauth2/token")
                .pathMapping("/oauth/revoke", "/oauth2/revoke");
    }

    // configure endpoints
    @Bean
    public TokenGranter tokenGranter() {
        return new CompositeTokenGranter(getDefaultTokenGranters(oAuth2RequestFactory()));
    }

    private OAuth2RequestFactory oAuth2RequestFactory() {
        return new DefaultOAuth2RequestFactory(clientService);
    }

    private List<TokenGranter> getDefaultTokenGranters(OAuth2RequestFactory oAuth2RequestFactory) {
        return List.of(
          new AuthorizationCodeTokenGranter(tokenServices, authorizationCodeServices, clientService, oAuth2RequestFactory),
          new RefreshTokenGranter(tokenServices, clientService, oAuth2RequestFactory),
          new ImplicitTokenGranter(tokenServices, clientService, oAuth2RequestFactory),
          new ClientCredentialsTokenGranter(tokenServices, clientService, oAuth2RequestFactory),
          new ResourceOwnerPasswordTokenGranter(authenticationManager, tokenServices, clientService, oAuth2RequestFactory));
    }

}
