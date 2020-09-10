package com.benoly.auth.errors;

import org.springframework.boot.web.server.WebServerException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MalformedRequestException extends WebServerException {

    public MalformedRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
