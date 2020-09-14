package dev.rexijie.auth.model.token;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSAKeyPairHolder implements KeyPairHolder {

    private final String id;
    private final KeyPair keyPair;


    public RSAKeyPairHolder(String id, KeyPair keyPair) {
        this.id = id;
        this.keyPair = keyPair;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public KeyPair getKeyPair() {
        return this.keyPair;
    }

    @Override
    public RSAPublicKey getPublicKey() {
        return (RSAPublicKey) keyPair.getPublic();
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return (RSAPrivateKey) keyPair.getPrivate();
    }
}
