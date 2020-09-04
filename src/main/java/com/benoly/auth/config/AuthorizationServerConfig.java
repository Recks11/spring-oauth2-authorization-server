package com.benoly.auth.config;

import com.benoly.auth.repository.AuthorizationTokenRepository;
import com.benoly.auth.service.ClientService;
import com.benoly.auth.service.UserService;
import com.benoly.auth.tokenservices.CustomAuthorizationCodeServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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

    public AuthorizationServerConfig(ClientService clientService,
                                     AuthenticationConfiguration authenticationConfiguration,
                                     AuthorizationServerTokenServices tokenServices,
                                     PasswordEncoder passwordEncoder,
                                     AuthorizationTokenRepository authorizationTokenRepository,
                                     UserService userService,
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
                .authorizationCodeServices(new CustomAuthorizationCodeServices(authorizationTokenRepository))
                .tokenServices(tokenServices);

        endpoints.addInterceptor(new SessionInvalidatingHandlerInterceptor());
    }


}

/**
 * This is interceptor invalidates sessions after the Authorization code flow is complete
 * To ensure the Authorization server is stateless
 */
@Slf4j
class SessionInvalidatingHandlerInterceptor extends HandlerInterceptorAdapter {

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && modelAndView.getView() instanceof RedirectView) {
            RedirectView redirectView = (RedirectView) modelAndView.getView();
            String redirectUrl = redirectView.getUrl();
            if (redirectUrl == null) return;
            if (redirectUrl.contains("code=") || redirectUrl.contains("error=") ) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    log.debug("invalidating session {}", session.getId());
                    session.invalidate();
                }
            }
        }
     }
}