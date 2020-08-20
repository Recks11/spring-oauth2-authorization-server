package com.benoly.auth.util;

import lombok.NonNull;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.SerializationUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

public class TokenUtils {
    public static String serializeAuthentication(@NonNull OAuth2Authentication auth2Authentication) {
        // TODO - handle null authentication
        var authenticationByteArray = SerializationUtils.serialize(auth2Authentication);
        return Base64.getEncoder().encodeToString(authenticationByteArray);
    }

    public static OAuth2Authentication deserializeAuthentication(String authentication) {
        var authenticationBytes = Base64.getDecoder().decode(authentication);
        var deserializedAuthentication = SerializationUtils.deserialize(authenticationBytes);
        if (!(deserializedAuthentication instanceof OAuth2Authentication))
            throw new RuntimeException("invalid authentication object");

        return (OAuth2Authentication) deserializedAuthentication;
    }

    public static String generateHash(String value) {
        if (value == null) return null;
        try {
            var md5Digest = MessageDigest.getInstance("MD5");
            var tokenBytes = value.getBytes(StandardCharsets.UTF_8);
            tokenBytes = md5Digest.digest(tokenBytes);
            return String.format("%032x", new BigInteger(1, tokenBytes));

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("MD5 algorithm not available");
        }
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}
