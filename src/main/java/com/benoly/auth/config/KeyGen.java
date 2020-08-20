package com.benoly.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.security.*;

@Component
public class KeyGen {

    @Bean
    public KeyPair rsaKeys() throws Exception {
        return generateKeys();
    }

    public static KeyPair generateKeys() throws NoSuchAlgorithmException {
        Provider provider = KeyFactory.getInstance("RSA").getProvider();
        return KeyPairGenerator.getInstance("RSA", provider).generateKeyPair();
    }
}
