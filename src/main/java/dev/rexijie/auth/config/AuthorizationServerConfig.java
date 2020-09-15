package dev.rexijie.auth.config;

import dev.rexijie.auth.config.interceptors.SessionInvalidatingHandlerInterceptor;
import dev.rexijie.auth.repository.AuthorizationTokenRepository;
import dev.rexijie.auth.service.ClientService;
import dev.rexijie.auth.service.UserService;
import dev.rexijie.auth.tokenservices.PersistentAuthorizationCodeServices;
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
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.time.Duration;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    private final UserService userService;
    private final ClientService clientService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthorizationTokenRepository authorizationTokenRepository;
    private final AuthorizationServerTokenServices tokenServices;
    private final AccessTokenConverter accessTokenConverter;

    public AuthorizationServerConfig(UserService userService,
                                     ClientService clientService,
                                     PasswordEncoder passwordEncoder,
                                     AuthenticationConfiguration authenticationConfiguration,
                                     AuthorizationTokenRepository authorizationTokenRepository,
                                     AuthorizationServerTokenServices tokenServices,
                                     AccessTokenConverter accessTokenConverter) throws Exception {
        this.userService = userService;
        this.tokenServices = tokenServices;
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
        this.authorizationTokenRepository = authorizationTokenRepository;
        this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
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

        source.registerCorsConfiguration("/oauth/token", corsConfig);
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
                .authorizationCodeServices(new PersistentAuthorizationCodeServices(authorizationTokenRepository))
                .tokenServices(tokenServices)
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS);

        endpoints.addInterceptor(new SessionInvalidatingHandlerInterceptor());
    }


}
