package dev.rexijie.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class WebConfig {

    /**
     * Cors Configuration
     * This is currently set to allow all methods from all origins
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter(
            @Qualifier("urlBasedCorsConfig") CorsConfigurationSource corsConfigurationSource) {
        var bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        bean.setOrder(0);
        return bean;
    }
}
