package dev.rexijie.auth.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

public class WebConfig {

    private final CorsConfigurationSource corsConfigurationSource;

    public WebConfig(@Qualifier("urlBasedCorsConfig") CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    /**
     * Cors Configuration
     * This is currently set to allow all methods from all origins
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        var bean = new FilterRegistrationBean<>(new CorsFilter(corsConfigurationSource));
        bean.setOrder(0);
        return bean;
    }
}
