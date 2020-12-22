package dev.rexijie.auth.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Factory for generating random String keys.
 * It makes use of {@link SecureRandom} to generate random bytes
 * of a given length
 *
 * @author Rex Ijiekhuamen
 */

@Slf4j
@Component
public class ClientSecretGenerator implements SecretGenerator {
    final int DEFAULT_KEY_LENGTH = 32;
    private final int bytesKeyLength;

    public ClientSecretGenerator() {
        this.bytesKeyLength = this.DEFAULT_KEY_LENGTH;
    }

    public ClientSecretGenerator(int bytesKeyLength) {
        this.bytesKeyLength = bytesKeyLength;
    }

    @Override
    public String generate() {
        return generate(bytesKeyLength);
    }

    @Override
    public String generate(int length) {
        char[] charEncodedBytes = Hex.encode(generateBytes(length));
        return new String(charEncodedBytes);
    }

    private byte[] generateBytes(int byteLength) {
        SecureRandom secureRandom;
        try {
            secureRandom = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException ex) {
            log.warn("No Strong secure algorithm available in JDK, switching to default instance");
            secureRandom = new SecureRandom();
        }

        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);

        return bytes;
    }
}
