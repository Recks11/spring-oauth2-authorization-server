package dev.rexijie.auth.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static dev.rexijie.auth.util.TokenUtils.getTokenFromAuthorizationHeader;

@Slf4j
@Component
public class ApiEndpointAuthenticationFilter extends OncePerRequestFilter {

    private final ResourceServerTokenServices tokenServices;
    private final ObjectMapper objectMapper;
    private final Set<String> ignoredPaths = new HashSet<>();

    public ApiEndpointAuthenticationFilter(
            ObjectMapper objectMapper,
            ResourceServerTokenServices resourceServerTokenServices) {
        this.objectMapper = objectMapper;
        this.tokenServices = resourceServerTokenServices;
        ignoredPaths.add("/oauth");
        ignoredPaths.add("/oauth2");
        ignoredPaths.add("/openid");
        ignoredPaths.add("/css");
        ignoredPaths.add("/js");
        ignoredPaths.add("/img");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        String token;
        String path = request.getRequestURI();

        if ((authorization != null && authorization.contains("Bearer")) && !pathShouldBeIgnored(path)) {
            try {
                token = getTokenFromAuthorizationHeader(authorization);

                OAuth2AccessToken oAuth2AccessToken = tokenServices.readAccessToken(token);
                if (oAuth2AccessToken.isExpired()) throw new InvalidTokenException("Token has expired");
                Authentication authentication = tokenServices.loadAuthentication(token);

                SecurityContext context = SecurityContextHolder.getContext();
                context.setAuthentication(authentication);
            } catch (InvalidTokenException ex) {
                writeErrorResponse(request, response, ex);
                return;
            }

        }
        chain.doFilter(request, response);
    }

    protected void writeErrorResponse(HttpServletRequest request,
                                      HttpServletResponse response,
                                      Exception exception) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Methods","POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age","*");
        response.setHeader("Access-Control-Allow-Headers","x-requested-with, authorization, Content-Type, Authorization, credential, X-XSRF-TOKEN");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        log.warn("Token Expired: {}", exception.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "invalid_token");
        errorResponse.put("error_description", exception.getMessage());

        response.getWriter()
                .write(objectMapper.writeValueAsString(errorResponse));
    }

    private boolean pathShouldBeIgnored(String path) {
        return ignoredPaths
                .stream()
                .anyMatch(path::startsWith);
    }
}
