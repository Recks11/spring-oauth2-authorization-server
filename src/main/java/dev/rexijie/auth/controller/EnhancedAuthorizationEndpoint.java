package dev.rexijie.auth.controller;

/*
 * https://openid.net/specs/openid-connect-core-1_0.html
 */

import dev.rexijie.auth.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.*;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.approval.DefaultUserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.security.oauth2.provider.implicit.ImplicitTokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.DefaultSessionAttributeStore;
import org.springframework.web.bind.support.SessionAttributeStore;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.security.Principal;
import java.util.*;

/**
 * Extension of springs {@link AuthorizationEndpoint} that supports the generation of ID tokens
 * according to the OpenID spec
 *
 * @author Rex Ijiekhuamen
 */
@Controller
@SessionAttributes({EnhancedAuthorizationEndpoint.AUTHORIZATION_REQUEST_ATTR_NAME, EnhancedAuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME})
public class EnhancedAuthorizationEndpoint extends AuthorizationEndpoint {
    static final String AUTHORIZATION_REQUEST_ATTR_NAME = "authorizationRequest";

    static final String ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME = "org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint.ORIGINAL_AUTHORIZATION_REQUEST";

    private AuthorizationCodeServices authorizationCodeServices;

    final
    ClientService clientService;
    final
    TokenGranter tokenGranter;

    private RedirectResolver redirectResolver = new DefaultRedirectResolver();

    private UserApprovalHandler userApprovalHandler = new DefaultUserApprovalHandler();

    private SessionAttributeStore sessionAttributeStore = new DefaultSessionAttributeStore();

    private OAuth2RequestValidator oauth2RequestValidator = new DefaultOAuth2RequestValidator();

    private String userApprovalPage = "forward:/oauth/confirm_access";

    private String errorPage = "forward:/oauth/error";

    private final Object implicitLock = new Object();

    public EnhancedAuthorizationEndpoint(AuthorizationCodeServices authorizationCodeServices, ClientService clientService, TokenGranter tokenGranter) {
        setAuthorizationCodeServices(authorizationCodeServices);
        setClientDetailsService(clientService);
        setTokenGranter(tokenGranter);
        this.clientService = clientService;
        this.tokenGranter = tokenGranter;
    }

    @RequestMapping("/oauth2/authorize")
    @Override
    public ModelAndView authorize(Map<String, Object> model,
                                  @RequestParam Map<String, String> parameters,
                                  SessionStatus sessionStatus, Principal principal) {
        AuthorizationRequest authorizationRequest = getOAuth2RequestFactory().createAuthorizationRequest(parameters);
        Set<String> responseTypes = authorizationRequest.getResponseTypes();

        // xor to handle either it contains code or token but not both
        if (containsOnly(responseTypes, "token") ^ containsOnly(responseTypes, "code"))
            return super.authorize(model, parameters, sessionStatus, principal);


        if (authorizationRequest.getClientId() == null)
            throw new InvalidClientException("A client id must be provided");

        if (authorizationRequest.getRequestParameters().get("nonce") == null)
            throw new InvalidRequestException("The nonce parameter is required for this flow");

        try {

            if (!(principal instanceof Authentication) || !((Authentication) principal).isAuthenticated()) {
                throw new InsufficientAuthenticationException(
                        "User must be authenticated with Spring Security before authorization can be completed.");
            }

            ClientDetails client = getClientDetailsService().loadClientByClientId(authorizationRequest.getClientId());

            // The resolved redirect URI is either the redirect_uri from the parameters or the one from
            // clientDetails. Either way we need to store it on the AuthorizationRequest.
            String redirectUriParameter = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
            String resolvedRedirect = redirectResolver.resolveRedirect(redirectUriParameter, client);
            if (!StringUtils.hasText(resolvedRedirect)) {
                throw new RedirectMismatchException(
                        "A redirectUri must be either supplied or preconfigured in the ClientDetails");
            }
            authorizationRequest.setRedirectUri(resolvedRedirect);

            // validate request params requested by the client
            oauth2RequestValidator.validateScope(authorizationRequest, client);

            // check for pre-approved requests
            // personal note: Literally not needed
            authorizationRequest = userApprovalHandler.checkForPreApproval(authorizationRequest, (Authentication) principal);

            // this is also useless. but it is useful if the userApproval handler is changed
            boolean approved = userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
            authorizationRequest.setApproved(approved);

            if (approved) {
                if (responseTypes.contains("code") && responseTypes.contains("id_token")) {
                    return new ModelAndView(getAuthorizationCodeResponse(authorizationRequest, (Authentication) principal));
                }

                return getIdTokenGrantResponse(authorizationRequest, (Authentication) principal);
            }

            // Store authorizationRequest AND an immutable Map of authorizationRequest in session
            // which will be used to validate against in approveOrDeny()
            model.put(AUTHORIZATION_REQUEST_ATTR_NAME, authorizationRequest);
            model.put(ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME, unmodifiableMap(authorizationRequest));

            return getUserApprovalPageResponse(model, authorizationRequest, (Authentication) principal);
        } catch (RuntimeException ex) {
            sessionStatus.setComplete();
            throw ex;
        }
    }

    // do super
    @RequestMapping(value = "/oauth2/authorize", method = RequestMethod.POST, params = OAuth2Utils.USER_OAUTH_APPROVAL)
    @Override
    public View approveOrDeny(@RequestParam Map<String, String> approvalParameters, Map<String, ?> model,
                              SessionStatus sessionStatus, Principal principal) {
        if (!(principal instanceof Authentication)) {
            sessionStatus.setComplete();
            throw new InsufficientAuthenticationException(
                    "User must be authenticated with Spring Security before authorizing an access token.");
        }

        AuthorizationRequest authorizationRequest = (AuthorizationRequest) model.get(AUTHORIZATION_REQUEST_ATTR_NAME);

        if (authorizationRequest == null) {
            sessionStatus.setComplete();
            throw new InvalidRequestException("Cannot approve uninitialized authorization request.");
        }

        // Check to ensure the Authorization Request was not modified during the user approval step
        @SuppressWarnings("unchecked")
        Map<String, Object> originalAuthorizationRequest = (Map<String, Object>) model.get(ORIGINAL_AUTHORIZATION_REQUEST_ATTR_NAME);
        if (isAuthorizationRequestModified(authorizationRequest, originalAuthorizationRequest)) {
            throw new InvalidRequestException("Changes were detected from the original authorization request.");
        }

        try {
            Set<String> responseTypes = authorizationRequest.getResponseTypes();
            // call super for everything other than the id_token response type
            if (responseTypes.contains("token") ^ responseTypes.contains("code")) {
                return super.approveOrDeny(approvalParameters, model, sessionStatus, principal);
            }

            authorizationRequest.setApprovalParameters(approvalParameters);
            authorizationRequest = userApprovalHandler.updateAfterApproval(authorizationRequest,
                    (Authentication) principal);
            boolean approved = userApprovalHandler.isApproved(authorizationRequest, (Authentication) principal);
            authorizationRequest.setApproved(approved);

            if (authorizationRequest.getRedirectUri() == null) {
                sessionStatus.setComplete();
                throw new InvalidRequestException("Cannot approve request when no redirect URI is provided.");
            }

            if (!authorizationRequest.isApproved()) {
                return new RedirectView(getUnsuccessfulRedirect(authorizationRequest,
                        new UserDeniedAuthorizationException("User denied access"), responseTypes.contains("token")),
                        false, true, false);
            }

            if (responseTypes.contains("code"))
                return getAuthorizationCodeResponse(authorizationRequest, (Authentication) principal);

            return getIdTokenGrantResponse(authorizationRequest, (Authentication) principal).getView();
        } finally {
            sessionStatus.setComplete();
        }
    }

    // copied from super
    // hecks if the authorization request is modified
    private boolean isAuthorizationRequestModified(
            AuthorizationRequest authorizationRequest, Map<String, Object> originalAuthorizationRequest) {
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getClientId(),
                originalAuthorizationRequest.get(OAuth2Utils.CLIENT_ID))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getState(),
                originalAuthorizationRequest.get(OAuth2Utils.STATE))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getRedirectUri(),
                originalAuthorizationRequest.get(OAuth2Utils.REDIRECT_URI))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getResponseTypes(),
                originalAuthorizationRequest.get(OAuth2Utils.RESPONSE_TYPE))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getScope(),
                originalAuthorizationRequest.get(OAuth2Utils.SCOPE))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.isApproved(),
                originalAuthorizationRequest.get("approved"))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getResourceIds(),
                originalAuthorizationRequest.get("resourceIds"))) {
            return true;
        }
        if (!ObjectUtils.nullSafeEquals(
                authorizationRequest.getAuthorities(),
                originalAuthorizationRequest.get("authorities"))) {
            return true;
        }

        return false;
    }

    /**
     * Gets a token using the implicit grant.
     *
     * @return an access token
     */
    private OAuth2AccessToken getTokenFromImplicitGrant(AuthorizationRequest authorizationRequest) {
        TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(authorizationRequest, "implicit");
        OAuth2Request storedOAuth2Request = getDefaultOAuth2RequestFactory().createOAuth2Request(authorizationRequest);
        OAuth2AccessToken token = getAccessTokenFromImplicitGrant(tokenRequest, storedOAuth2Request);

        if (token == null) {
            throw new UnsupportedResponseTypeException("Unsupported response type: id_token");
        }
        return token;
    }

    /**
     * gets the response model and view containing the Tokens for the implicit grant.
     * the actual URL generation is done in the call to append
     */
    private ModelAndView getIdTokenGrantResponse(AuthorizationRequest authorizationRequest,
                                                 Authentication principal) {
        try {

            OAuth2AccessToken token = getTokenFromImplicitGrant(authorizationRequest);

            return new ModelAndView(new RedirectView(appendAccessToken(authorizationRequest, token), false,
                    true, false));
        } catch (OAuth2Exception ex) {
            return new ModelAndView(new RedirectView(getUnsuccessfulRedirect(authorizationRequest, ex, true), false,
                    true, false));
        }
    }

    /**
     * Generates the implicit grant token using the token request and the initial OAUth2 request
     *
     * @param tokenRequest        the implicit grant token request
     * @param storedOAuth2Request the OAuth2 request used to initiate the flow
     */
    private OAuth2AccessToken getAccessTokenFromImplicitGrant(TokenRequest tokenRequest, OAuth2Request storedOAuth2Request) {
        OAuth2AccessToken accessToken;
        synchronized (this.implicitLock) {
            accessToken = getTokenGranter().grant("implicit",
                    new ImplicitTokenRequest(tokenRequest, storedOAuth2Request));
        }
        return accessToken;
    }

    // generate the Authorization code response
    private View getAuthorizationCodeResponse(AuthorizationRequest authorizationRequest, Authentication authUser) {
        try {
            if (isIdTokenRequest(authorizationRequest) | isImplicitRequest(authorizationRequest)) {
                TokenRequest tokenRequest = getOAuth2RequestFactory().createTokenRequest(authorizationRequest, "implicit");
                OAuth2Request storedOAuth2Request = getDefaultOAuth2RequestFactory().createOAuth2Request(authorizationRequest);
                OAuth2AccessToken accessToken = getAccessTokenFromImplicitGrant(tokenRequest, storedOAuth2Request);
                if (accessToken == null) {
                    throw new UnsupportedResponseTypeException("Unsupported response type: id_token");
                }

                return new RedirectView(getSuccessfulRedirect(authorizationRequest,
                        generateCode(authorizationRequest, authUser), accessToken), false, true, false);
            }
            return new RedirectView(getSuccessfulRedirect(authorizationRequest,
                    generateCode(authorizationRequest, authUser), null), false, true, false);
        } catch (OAuth2Exception e) {
            return new RedirectView(getUnsuccessfulRedirect(authorizationRequest, e, false), false, true, false);
        }
    }

    private String generateCode(AuthorizationRequest authorizationRequest, Authentication authentication)
            throws AuthenticationException {

        try {

            OAuth2Request storedOAuth2Request = getOAuth2RequestFactory().createOAuth2Request(authorizationRequest);

            OAuth2Authentication combinedAuth = new OAuth2Authentication(storedOAuth2Request, authentication);
            String code = authorizationCodeServices.createAuthorizationCode(combinedAuth);

            return code;

        } catch (OAuth2Exception e) {

            if (authorizationRequest.getState() != null) {
                e.addAdditionalInformation("state", authorizationRequest.getState());
            }

            throw e;

        }
    }

    // generate the suthorization code url fragment. The ID token should be appended here
    private String getSuccessfulRedirect(AuthorizationRequest authorizationRequest, String authorizationCode, OAuth2AccessToken token) {

        if (authorizationCode == null) {
            throw new IllegalStateException("No authorization code found in the current request scope.");
        }

        boolean fragmentUrl = false;

        Map<String, String> query = new LinkedHashMap<String, String>();
        query.put("code", authorizationCode);

        String state = authorizationRequest.getState();
        if (state != null) {
            query.put("state", state);
        }

        if (isIdTokenRequest(authorizationRequest)) { // append the id_token if the grant exists
            if (token != null && token.getAdditionalInformation().get("id_token") != null) {
                query.put("id_token", (String) token.getAdditionalInformation().get("id_token"));
                fragmentUrl = true;
            }
        }

        if (isImplicitRequest(authorizationRequest)) { // append the token if the grant type contains code
            if (token != null) {
                query.put("access_token", token.getValue());
                query.put("token_type", token.getTokenType());
                Date expiration = token.getExpiration();
                if (expiration != null) {
                    long expires_in = (expiration.getTime() - System.currentTimeMillis()) / 1000;
                    query.put("expires_in", String.valueOf(expires_in));
                }

                String originalScope = authorizationRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
                if (originalScope == null || !OAuth2Utils.parseParameterList(originalScope).equals(token.getScope())) {
                    query.put("scope", OAuth2Utils.formatParameterList(token.getScope()));
                }
                fragmentUrl = true;
            }

        }

        return append(authorizationRequest.getRedirectUri(), query, fragmentUrl);
    }

    /**
     * This is where the access token is generated
     * url encoded id_token is appended here
     * handles response types id_token, token id_token
     */
    private String appendAccessToken(AuthorizationRequest authorizationRequest, OAuth2AccessToken accessToken) {
        Set<String> responseTypes = authorizationRequest.getResponseTypes();

        Map<String, String> keys = new HashMap<String, String>();
        Map<String, Object> vars = new LinkedHashMap<String, Object>();

        if (containsOnly(responseTypes, "id_token")) { // handle response type id_token
            vars.put("id_token", accessToken.getAdditionalInformation().get("id_token"));
            vars.put("state", authorizationRequest.getState());
            return append(authorizationRequest.getRedirectUri(), vars, keys, true);
        }

        if (accessToken == null) {
            throw new InvalidRequestException("An implicit grant could not be made");
        }

        if (authorizationRequest.getResponseTypes().contains("token")) {
            vars.put("access_token", accessToken.getValue());
        }

        vars.put("token_type", accessToken.getTokenType());
        String state = authorizationRequest.getState();

        if (state != null) {
            vars.put("state", state);
        }
        Date expiration = accessToken.getExpiration();
        if (expiration != null) {
            long expires_in = (expiration.getTime() - System.currentTimeMillis()) / 1000;
            vars.put("expires_in", expires_in);
        }
        String originalScope = authorizationRequest.getRequestParameters().get(OAuth2Utils.SCOPE);
        if (originalScope == null || !OAuth2Utils.parseParameterList(originalScope).equals(accessToken.getScope())) {
            vars.put("scope", OAuth2Utils.formatParameterList(accessToken.getScope()));
        }

        // add the id token
        vars.put("id_token", accessToken.getAdditionalInformation().get("id_token"));

        Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
        for (String key : additionalInformation.keySet()) {
            Object value = additionalInformation.get(key);
            if (value == "id_token") continue; // skip the id_token from additional params
            if (value != null) {
                keys.put("extra_" + key, key);
                vars.put("extra_" + key, value);
            }
        }
        // Do not include the refresh token (even if there is one)
        return append(authorizationRequest.getRedirectUri(), vars, keys, true);
    }

    Map<String, Object> unmodifiableMap(AuthorizationRequest authorizationRequest) {
        Map<String, Object> authorizationRequestMap = new HashMap<String, Object>();

        authorizationRequestMap.put(OAuth2Utils.CLIENT_ID, authorizationRequest.getClientId());
        authorizationRequestMap.put(OAuth2Utils.STATE, authorizationRequest.getState());
        authorizationRequestMap.put(OAuth2Utils.REDIRECT_URI, authorizationRequest.getRedirectUri());
        if (authorizationRequest.getResponseTypes() != null) {
            authorizationRequestMap.put(OAuth2Utils.RESPONSE_TYPE,
                    Set.copyOf(authorizationRequest.getResponseTypes()));
        }
        if (authorizationRequest.getScope() != null) {
            authorizationRequestMap.put(OAuth2Utils.SCOPE,
                    Set.copyOf(authorizationRequest.getScope()));
        }
        authorizationRequestMap.put("approved", authorizationRequest.isApproved());
        if (authorizationRequest.getResourceIds() != null) {
            authorizationRequestMap.put("resourceIds",
                    Set.copyOf(authorizationRequest.getResourceIds()));
        }
        if (authorizationRequest.getAuthorities() != null) {
            authorizationRequestMap.put("authorities",
                    Set.<GrantedAuthority>copyOf(authorizationRequest.getAuthorities()));
        }

        return Collections.unmodifiableMap(authorizationRequestMap);
    }

    // copied from super
    private ModelAndView getUserApprovalPageResponse(Map<String, Object> model,
                                                     AuthorizationRequest authorizationRequest, Authentication principal) {
        if (logger.isDebugEnabled()) {
            logger.debug("Loading user approval page: " + userApprovalPage);
        }
        model.putAll(userApprovalHandler.getUserApprovalRequest(authorizationRequest, principal));
        return new ModelAndView(userApprovalPage, model);
    }

    // copied from super
    private String getUnsuccessfulRedirect(AuthorizationRequest authorizationRequest, OAuth2Exception failure,
                                           boolean fragment) {

        if (authorizationRequest == null || authorizationRequest.getRedirectUri() == null) {
            // we have no redirect for the user. very sad.
            throw new UnapprovedClientAuthenticationException("Authorization failure, and no redirect URI.", failure);
        }

        Map<String, String> query = new LinkedHashMap<String, String>();

        query.put("error", failure.getOAuth2ErrorCode());
        query.put("error_description", failure.getMessage());

        if (authorizationRequest.getState() != null) {
            query.put("state", authorizationRequest.getState());
        }

        if (failure.getAdditionalInformation() != null) {
            for (Map.Entry<String, String> additionalInfo : failure.getAdditionalInformation().entrySet()) {
                query.put(additionalInfo.getKey(), additionalInfo.getValue());
            }
        }

        return append(authorizationRequest.getRedirectUri(), query, fragment);

    }

    // copied from super
    private String append(String base, Map<String, ?> query, boolean fragment) {
        return append(base, query, null, fragment);
    }

    // copied from super
    // This is where the URL is generated
    private String append(String base, Map<String, ?> query, Map<String, String> keys, boolean fragment) {

        UriComponentsBuilder template = UriComponentsBuilder.newInstance();
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(base);
        URI redirectUri;
        try {
            // assume it's encoded to start with (if it came in over the wire)
            redirectUri = builder.build(true).toUri();
        } catch (Exception e) {
            // ... but allow client registrations to contain hard-coded non-encoded values
            redirectUri = builder.build().toUri();
            builder = UriComponentsBuilder.fromUri(redirectUri);
        }
        template.scheme(redirectUri.getScheme()).port(redirectUri.getPort()).host(redirectUri.getHost())
                .userInfo(redirectUri.getUserInfo()).path(redirectUri.getPath());

        if (fragment) {
            StringBuilder values = new StringBuilder();
            if (redirectUri.getFragment() != null) {
                String append = redirectUri.getFragment();
                values.append(append);
            }
            for (String key : query.keySet()) {
                if (values.length() > 0) {
                    values.append("&");
                }
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                values.append(name)
                        .append("={")
                        .append(key)
                        .append("}");
            }
            if (values.length() > 0) {
                template.fragment(values.toString());
            }
            UriComponents encoded = template.build().expand(query).encode();
            builder.fragment(encoded.getFragment());
        } else {
            for (String key : query.keySet()) {
                String name = key;
                if (keys != null && keys.containsKey(key)) {
                    name = keys.get(key);
                }
                template.queryParam(name, "{" + key + "}");
            }
            template.fragment(redirectUri.getFragment());
            UriComponents encoded = template.build().expand(query).encode();
            builder.query(encoded.getQuery());
        }

        return builder.build().toUriString();

    }

    public void setUserApprovalPage(String userApprovalPage) {
        this.userApprovalPage = userApprovalPage;
    }

    @Override
    public void setAuthorizationCodeServices(AuthorizationCodeServices authorizationCodeServices) {
        super.setAuthorizationCodeServices(authorizationCodeServices);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    public void setRedirectResolver(RedirectResolver redirectResolver) {
        super.setRedirectResolver(redirectResolver);
        this.redirectResolver = redirectResolver;
    }

    public void setUserApprovalHandler(UserApprovalHandler userApprovalHandler) {
        super.setUserApprovalHandler(userApprovalHandler);
        this.userApprovalHandler = userApprovalHandler;
    }

    public void setOAuth2RequestValidator(OAuth2RequestValidator oauth2RequestValidator) {
        super.setOAuth2RequestValidator(oauth2RequestValidator);
        this.oauth2RequestValidator = oauth2RequestValidator;
    }

    @Override
    public void setSessionAttributeStore(SessionAttributeStore sessionAttributeStore) {
        super.setSessionAttributeStore(sessionAttributeStore);
        this.sessionAttributeStore = sessionAttributeStore;
    }

    public void setOauth2RequestValidator(OAuth2RequestValidator oauth2RequestValidator) {
        super.setOAuth2RequestValidator(oauth2RequestValidator);
        this.oauth2RequestValidator = oauth2RequestValidator;
    }

    @Override
    public void setErrorPage(String errorPage) {
        super.setErrorPage(errorPage);
        this.errorPage = errorPage;
    }


    private <T> boolean containsOnly(Collection<T> collection, T item) {
        return collection.size() == 1 && collection.contains(item);
    }

    private boolean isIdTokenRequest(AuthorizationRequest authorizationRequest) {
        return authorizationRequest.getResponseTypes().contains("id_token");
    }

    private boolean isImplicitRequest(AuthorizationRequest authorizationRequest) {
        return authorizationRequest.getResponseTypes().contains("token");
    }

    @ExceptionHandler(ClientRegistrationException.class)
    public ModelAndView handleClientRegistrationException(Exception e, ServletWebRequest webRequest) throws Exception {
        logger.info("Handling ClientRegistrationException error: " + e.getMessage());
        return handleException(new BadClientCredentialsException(), webRequest);
    }

    @ExceptionHandler(OAuth2Exception.class)
    public ModelAndView handleOAuth2Exception(OAuth2Exception e, ServletWebRequest webRequest) throws Exception {
        logger.info("Handling OAuth2 error: " + e.getSummary());
        return handleException(e, webRequest);
    }

    @ExceptionHandler(HttpSessionRequiredException.class)
    public ModelAndView handleHttpSessionRequiredException(HttpSessionRequiredException e, ServletWebRequest webRequest)
            throws Exception {
        logger.info("Handling Session required error: " + e.getMessage());
        return handleException(new AccessDeniedException("Could not obtain authorization request from session", e),
                webRequest);
    }

    // copied from super
    private ModelAndView handleException(Exception e, ServletWebRequest webRequest) throws Exception {

        ResponseEntity<OAuth2Exception> translate = getExceptionTranslator().translate(e);
        webRequest.getResponse().setStatus(translate.getStatusCode().value());

        if (e instanceof ClientAuthenticationException || e instanceof RedirectMismatchException) {
            return new ModelAndView(errorPage, Collections.singletonMap("error", translate.getBody()));
        }

        AuthorizationRequest authorizationRequest = null;
        try {
            authorizationRequest = getAuthorizationRequestForError(webRequest);
            String requestedRedirectParam = authorizationRequest.getRequestParameters().get(OAuth2Utils.REDIRECT_URI);
            String requestedRedirect = redirectResolver.resolveRedirect(requestedRedirectParam,
                    getClientDetailsService().loadClientByClientId(authorizationRequest.getClientId()));
            authorizationRequest.setRedirectUri(requestedRedirect);
            String redirect = getUnsuccessfulRedirect(authorizationRequest, translate.getBody(), authorizationRequest
                    .getResponseTypes().contains("token"));
            return new ModelAndView(new RedirectView(redirect, false, true, false));
        } catch (OAuth2Exception ex) {
            // If an AuthorizationRequest cannot be created from the incoming parameters it must be
            // an error. OAuth2Exception can be handled this way. Other exceptions will generate a standard 500
            // response.
            return new ModelAndView(errorPage, Collections.singletonMap("error", translate.getBody()));
        }

    }

    // copied from super
    private AuthorizationRequest getAuthorizationRequestForError(ServletWebRequest webRequest) {

        // If it's already there then we are in the approveOrDeny phase and we can use the saved request
        AuthorizationRequest authorizationRequest = (AuthorizationRequest) sessionAttributeStore.retrieveAttribute(
                webRequest, AUTHORIZATION_REQUEST_ATTR_NAME);
        if (authorizationRequest != null) {
            return authorizationRequest;
        }

        Map<String, String> parameters = new HashMap<String, String>();
        Map<String, String[]> map = webRequest.getParameterMap();
        for (String key : map.keySet()) {
            String[] values = map.get(key);
            if (values != null && values.length > 0) {
                parameters.put(key, values[0]);
            }
        }

        try {
            return getOAuth2RequestFactory().createAuthorizationRequest(parameters);
        } catch (Exception e) {
            return getDefaultOAuth2RequestFactory().createAuthorizationRequest(parameters);
        }

    }
}
