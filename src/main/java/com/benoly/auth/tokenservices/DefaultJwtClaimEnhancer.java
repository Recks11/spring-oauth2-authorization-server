package com.benoly.auth.tokenservices;

import com.benoly.auth.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.benoly.auth.constants.Claims.JwtClaims.ROLE_CLAIM;
import static com.benoly.auth.constants.Claims.JwtClaims.USERNAME_CLAIM;

@PropertySource("classpath:tokenclaims.properties")
public class DefaultJwtClaimEnhancer implements JwtClaimsEnhancer {

    @Value("${auth.jwt.issuer:https://rexijie.dev}")
    private String issuer;

    private final UserService userService;

    public DefaultJwtClaimEnhancer(UserService userService) {
        this.userService = userService;
    }

    public Claims enhance(Map<String, Object> originalClaims) {
        Claims claims = new DefaultClaims(originalClaims);
        String userName = claims.get(USERNAME_CLAIM, String.class);
        claims.remove(USERNAME_CLAIM);

        var user = userService.findUserByUsername(userName);
        var role = user.getRole().getName();

        claims.setSubject(userName);
        claims.setIssuer(issuer);
        claims.put(ROLE_CLAIM, role);

        return claims;
    }
}
