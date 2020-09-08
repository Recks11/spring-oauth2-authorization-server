package com.benoly.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
/**
 * @author Rex Ijiekhuamen
 */
@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan({
        "com.benoly.auth.config",
        "com.benoly.auth.tokenservices"
})
public class Oauth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
    }
}
