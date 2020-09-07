package com.benoly.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.endpoint.CheckTokenEndpoint;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserInfoEndpoint {

    @Autowired
    private CheckTokenEndpoint checkTokenEndpoint;

    @RequestMapping("/user/info")
    private Map<String, ?> userInfo(@RequestHeader() String authorization) {
        String tokenValue = authorization.startsWith("Bearer ") ? authorization.substring(7) : null;

        return checkTokenEndpoint.checkToken(tokenValue);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        return checkTokenEndpoint.handleException(e);
    }
}
