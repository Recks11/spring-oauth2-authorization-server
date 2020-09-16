package dev.rexijie.auth.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.rexijie.auth.filters.ApiEndpointAuthenticationFilter;
import dev.rexijie.auth.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;
    private final ResourceServerTokenServices resourceServerTokenServices;

    public WebSecurityConfig(UserService userService,
                             PasswordEncoder passwordEncoder,
                             ObjectMapper objectMapper,
                             ResourceServerTokenServices tokenServices
                             ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
        this.resourceServerTokenServices = tokenServices;
    }

    /**
     * Cors Configuration
     * This is currently set to allow all methods from all origins
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/img/**", "/openid/**")
                .permitAll()
                .antMatchers("/api/**")
                .authenticated()
                .antMatchers("/oauth/authorize").denyAll()
                .and()
                .cors().configurationSource(corsConfigurationSource())
                .and().authorizeRequests().anyRequest().authenticated();


        http.formLogin()
                .loginPage("/oauth2/login").permitAll()
                .and()
                .logout()
                .logoutUrl("/oauth2/logout").permitAll();

        http.addFilterBefore(new ApiEndpointAuthenticationFilter(objectMapper, resourceServerTokenServices),
                UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Qualifier("urlBasedCorsConfig")
    CorsConfigurationSource corsConfigurationSource() {
        var source = new UrlBasedCorsConfigurationSource();
        var corsConfig = new CorsConfiguration();
        corsConfig.setAllowedMethods(List.of("GET", "POST", "DELETE", "PUT", "OPTIONS"));
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedOrigin("*");
        corsConfig.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfig);
        source.registerCorsConfiguration("/oauth2/token", corsConfig);

        return source;
    }

    //
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Bean("authenticationManagerBean")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
