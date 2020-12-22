package dev.rexijie.auth.model.token;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface KeyPairHolder {
    String getId();

    KeyPair getKeyPair();

    <K1 extends PublicKey> K1 getPublicKey();

    <K2 extends PrivateKey> K2 getPrivateKey();
}
