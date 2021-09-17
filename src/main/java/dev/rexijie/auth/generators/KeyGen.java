package dev.rexijie.auth.generators;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import dev.rexijie.auth.model.token.KeyPairHolder;
import dev.rexijie.auth.model.token.RSAKeyPairHolder;
import dev.rexijie.auth.service.SecretGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Component
public class KeyGen {
    private final SecretGenerator secretGenerator;

    public KeyGen(SecretGenerator secretGenerator) {
        this.secretGenerator = secretGenerator;
    }

    @Bean
    public KeyPairHolder<RSAPublicKey, RSAPrivateKey> rsaKeys() throws Exception {
        return new RSAKeyPairHolder(secretGenerator.generate(8), generateKeys());
    }

    @Bean
    public JWKSet jwkSet(KeyPairHolder<RSAPublicKey, RSAPrivateKey> keyPairHolder) {
        RSAKey.Builder builder = new RSAKey.Builder(keyPairHolder.getPublicKey())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID(keyPairHolder.getId());

        return new JWKSet(builder.build());
    }

    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
        Provider provider = KeyFactory.getInstance("RSA").getProvider();
        return KeyPairGenerator.getInstance("RSA", provider).generateKeyPair();
    }
}
