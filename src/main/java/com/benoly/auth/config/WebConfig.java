package com.benoly.auth.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

    /**
     * Cors Configuration
     * This is currently set to allow all methods from all origins
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        var source = new UrlBasedCorsConfigurationSource();
        var corsConfig = new CorsConfiguration();
        corsConfig.addAllowedMethod(HttpMethod.GET);
        corsConfig.addAllowedMethod(HttpMethod.POST);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.addAllowedMethod(HttpMethod.PUT);
//        corsConfig.addAllowedMethod(HttpMethod.OPTIONS);
        corsConfig.addAllowedHeader("*");
        corsConfig.addAllowedOrigin("*");
        source.registerCorsConfiguration("/**", corsConfig);

        var bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }
}
