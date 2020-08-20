package com.benoly.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class Api {

    private final ObjectMapper objectMapper;

    public Api(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @GetMapping("/api/me")
    @ResponseBody
    public ResponseEntity<String> home(@AuthenticationPrincipal Authentication authentication)  throws Exception{
        var as = objectMapper.writeValueAsString(authentication);
        return new ResponseEntity<>(as, HttpStatus.OK);
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}