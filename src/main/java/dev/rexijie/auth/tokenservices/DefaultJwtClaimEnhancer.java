package dev.rexijie.auth.tokenservices;

import dev.rexijie.auth.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.Map;

import static io.jsonwebtoken.Claims.ISSUED_AT;

public class DefaultJwtClaimEnhancer implements JwtClaimsEnhancer {
    @Value("${oauth2.openid.discovery.issuer:https://rexijie.dev}")
    private String issuer;

    private final UserService userService;

    public DefaultJwtClaimEnhancer(UserService userService) {
        this.userService = userService;
    }

    public Claims enhance(Map<String, Object> originalClaims) {
        Claims claims = new DefaultClaims(originalClaims);
        String userName = claims.get(dev.rexijie.auth.constants.Claims.JwtClaims.USERNAME_CLAIM, String.class);
        claims.remove(dev.rexijie.auth.constants.Claims.JwtClaims.USERNAME_CLAIM);

        var user = userService.findUserByUsername(userName);
        var role = user.getRole().getName();

        if (!claims.containsKey(ISSUED_AT))
            claims.setIssuedAt(new Date());
        claims.setSubject(userName);
        claims.setIssuer(issuer);
        claims.put(dev.rexijie.auth.constants.Claims.JwtClaims.ROLE_CLAIM, role);

        return claims;
    }
}
