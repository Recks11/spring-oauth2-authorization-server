package com.benoly.auth.errors;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

/**
 * Exception thrown when a request makes no sense
 * @author Rex Ijiekhuamen
 * 08 Sep 2020
 */
public class DumbRequestException extends ClientAuthenticationException {
    public DumbRequestException(String message) {
        super(message);
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "invalid_token";
    }
}
