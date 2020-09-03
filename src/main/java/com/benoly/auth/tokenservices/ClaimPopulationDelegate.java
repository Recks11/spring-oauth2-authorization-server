package com.benoly.auth.tokenservices;

import com.benoly.auth.model.User;
import com.benoly.auth.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

import static com.benoly.auth.tokenservices.JwtTokenConverter.ROLE_CLAIM;
import static com.benoly.auth.tokenservices.JwtTokenConverter.USERNAME_CLAIM;


public class ClaimPopulationDelegate {
    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;
    private final String FAMILY_NAME_CLAIM = "family_name";
    private final String GIVEN_NAME_CLAIM = "given_name";
    private final String BIRTH_DATE_CLAIM = "birthdate";
    private final String EMAIL_CLAIM = "email";
    private final String PHONE_CLAIM = "phone";
    private final String PICTURE_CLAIM = "picture";

    private final UserService userService;

    public ClaimPopulationDelegate(UserService userService) {
        this.userService = userService;
    }

    public Claims populateClaims(Map<String, Object> originalClaims) {
        Claims claims = new DefaultClaims(originalClaims);

        String userName = claims.get(USERNAME_CLAIM, String.class);
        var user = userService.findUserByUsername(userName);
        var role = user.getRole().getName();
        claims.remove(USERNAME_CLAIM);
        claims.setSubject(userName);
        claims.setIssuer(issuer);
        claims.put(ROLE_CLAIM, role);

        return claims;
    }

    private void populateIdClaims(Claims claims, User user) {
        var profile = user.getUserInfo();
        claims.put(FAMILY_NAME_CLAIM, profile.getLastName());
        claims.put(GIVEN_NAME_CLAIM, profile.getFirstName());
        claims.put(BIRTH_DATE_CLAIM, profile.getDataOfBirth());
        claims.put(EMAIL_CLAIM, profile.getEmail());
        claims.put(PHONE_CLAIM, profile.getPhoneNumber());
//
    }

    public Claims removeClaims(Claims claims, String... keys) {
        for (String key: keys)
            claims.remove(key);

        return claims;
    }
}
