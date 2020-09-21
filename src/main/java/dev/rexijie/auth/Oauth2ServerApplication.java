package dev.rexijie.auth;

import dev.rexijie.auth.config.OIDCDiscovery;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * @author Rex Ijiekhuamen
 */
@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = {OIDCDiscovery.class})
public class Oauth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
    }
}

/*
    things left to do for the base implementation as per
    https://openid.net/specs/openid-connect-core-1_0.html#ImplementationConsiderations
    TODO
        - Implement prompt parameter
        - implement display parameter
        - implement preferred locales
        - implement max_age
        - implement context-class-reference (acr_values)
    for Dynamic Client
    TODO
        - implement dynamic registration
        - implement Request URI (request_uri)

 */