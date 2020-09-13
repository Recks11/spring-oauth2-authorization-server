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

/*
    things left to do for the base implementation as per
    https://openid.net/specs/openid-connect-core-1_0.html#ImplementationConsiderations
    TODO
        - Implement prompt parameter
        - implement display parameter
        - implement preferred locales
        - implement auth_time [DONE]
        - implement max_age
        - implement context-class-reference (acr_values)
    for Dynamic Client
    TODO
        - Implement response types
            - id_token / i don't want to tho, implicit is annoying /
            - code [DONE]
            - id_token token /i don't want to tho, implicit is annnoying/
        - Implement openid discovery [DONE / OnGoing]
        - implement dynamic registration
        - implement userinfo endpoint [DONE]
        - publish public keys as bare keys [Done]
        - implement Request URI (request_uri)

 */