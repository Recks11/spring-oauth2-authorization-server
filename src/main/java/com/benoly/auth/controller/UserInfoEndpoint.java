package com.benoly.auth.controller;

import com.benoly.auth.model.User;
import com.benoly.auth.model.UserInfo;
import com.benoly.auth.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.benoly.auth.util.ObjectUtils.cleanMap;
import static com.benoly.auth.util.ObjectUtils.toMap;
import static io.jsonwebtoken.Claims.SUBJECT;

@RestController
public class UserInfoEndpoint {

    private final ResourceServerTokenServices resourceServerTokenServices;
    private final AccessTokenConverter accessTokenConverter;
    private final UserService userService;


    private WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    public UserInfoEndpoint(ResourceServerTokenServices resourceServerTokenServices,
                            AccessTokenConverter accessTokenConverter,
                            UserService userService) {
        this.resourceServerTokenServices = resourceServerTokenServices;
        this.accessTokenConverter = accessTokenConverter;
        this.userService = userService;
    }

    /**
     * @param exceptionTranslator the exception translator to set
     */
    public void setExceptionTranslator(WebResponseExceptionTranslator<OAuth2Exception> exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }

    @RequestMapping("/user/info")
    private Map<String, ?> userInfo(@RequestHeader() String authorization) {
        String tokenValue = authorization.startsWith("Bearer ") ? authorization.substring(7) : null;

        OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(tokenValue);
        if (token == null) throw new InvalidTokenException("Token was not recognised");
        if (token.isExpired()) throw new InvalidTokenException("Token has expired");

        OAuth2Authentication auth2Authentication = resourceServerTokenServices.loadAuthentication(token.getValue());
        Map<String, ?> claims = accessTokenConverter.convertAccessToken(token, auth2Authentication);

        String subject = claims.get(SUBJECT).toString();
        User user = userService.findUserByUsername(subject);

        UserInfo userInfo = user.getUserInfo();
        Map<String, Object> userInfoMap = toMap(userInfo);
        userInfoMap.put(SUBJECT, subject);
        cleanMap(userInfoMap);

        return userInfoMap;
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<OAuth2Exception> handleException(Exception e) throws Exception {
        InvalidTokenException e400 = new InvalidTokenException(e.getMessage()) {
            @Override
            public int getHttpErrorCode() {
                return 400;
            }
        };
        return exceptionTranslator.translate(e400);
    }
}
