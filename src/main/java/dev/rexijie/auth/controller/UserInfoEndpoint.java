package dev.rexijie.auth.controller;

import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.UserInfo;
import dev.rexijie.auth.service.UserService;
import dev.rexijie.auth.util.ObjectUtils;
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

import static io.jsonwebtoken.Claims.SUBJECT;

/**
 * @author Rex Ijiekhuamen
 */
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
    /*
     * Maybe add aggregated claims
     * that will link to other resources
     * https://openid.net/specs/openid-connect-core-1_0.html#UserInfo
     */
    @RequestMapping("/openid/userinfo")
    private Map<String, ?> userInfo(@RequestHeader("Authorization") String authorization) {
        String tokenValue = authorization.startsWith("Bearer ") ? authorization.substring(7) : null;

        OAuth2AccessToken token = resourceServerTokenServices.readAccessToken(tokenValue);
        if (tokenValue == null || token == null) throw new InvalidTokenException("Token was not recognised");
        if (token.isExpired()) throw new InvalidTokenException("Token has expired");

        OAuth2Authentication auth2Authentication = resourceServerTokenServices.loadAuthentication(token.getValue());
        Map<String, ?> claims = accessTokenConverter.convertAccessToken(token, auth2Authentication);

        String subject = claims.get(SUBJECT).toString();
        User user = userService.findUserByUsername(subject);

        UserInfo userInfo = user.getUserInfo();
        Map<String, Object> userInfoMap = ObjectUtils.toMap(userInfo);
        userInfoMap.put(SUBJECT, subject);

        return ObjectUtils.cleanMap(userInfoMap);
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
