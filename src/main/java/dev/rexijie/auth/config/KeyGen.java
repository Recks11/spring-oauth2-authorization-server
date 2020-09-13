package dev.rexijie.auth.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.interfaces.RSAPublicKey;

@Component
public class KeyGen {

    @Bean
    public KeyPair rsaKeys() throws Exception {
        return generateKeys();
    }

    @Bean
    public JWKSet jwkSet(KeyPair keyPair) {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID("9e96b669554474f9");

        return new JWKSet(builder.build());
    }

    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
        Provider provider = KeyFactory.getInstance("RSA").getProvider();
        return KeyPairGenerator.getInstance("RSA", provider).generateKeyPair();
    }
}
