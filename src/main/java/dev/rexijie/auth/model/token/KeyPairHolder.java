package dev.rexijie.auth.model.token;

import java.security.KeyPair;

public interface KeyPairHolder<K1, K2> {
    String getId();

    KeyPair getKeyPair();

    K1 getPublicKey();

    K2 getPrivateKey();
}
