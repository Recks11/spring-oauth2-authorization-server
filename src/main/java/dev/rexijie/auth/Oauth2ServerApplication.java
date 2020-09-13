package dev.rexijie.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
/**
 * @author Rex Ijiekhuamen
 */
@SpringBootApplication
@ConfigurationPropertiesScan({
        "dev.rexijie.auth.config"
})
public class Oauth2ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
    }
}
