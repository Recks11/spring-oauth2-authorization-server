package dev.rexijie.auth.tokenservices.openid;

import dev.rexijie.auth.constants.Scopes;
import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.token.IDToken;
import dev.rexijie.auth.model.token.KeyPairHolder;
import dev.rexijie.auth.service.UserService;
import dev.rexijie.auth.tokenservices.JwtTokenEnhancer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.*;

import static dev.rexijie.auth.util.TokenRequestUtils.isAuthorizationCodeRequest;
import static dev.rexijie.auth.util.TokenRequestUtils.isImplicitRequest;
import static dev.rexijie.auth.util.TokenUtils.getMessageDigestInstance;
import static dev.rexijie.auth.util.TokenUtils.hashString;
import static io.jsonwebtoken.Claims.AUDIENCE;
import static org.springframework.security.oauth2.core.oidc.IdTokenClaimNames.NONCE;

/**
 * @author Rex Ijiekhuamen
 */
public class IdTokenGeneratingTokenEnhancer extends JwtTokenEnhancer {

    private final IDTokenClaimsEnhancer enhancer;
    private final UserService userService;
    @Value("${oauth2.openid.implicit.enabled}")
    private final boolean implicitEnabled = false;

    public IdTokenGeneratingTokenEnhancer(UserService userService,
                                          IDTokenClaimsEnhancer enhancer,
                                          KeyPairHolder keyPairHolder) {
        super(keyPairHolder);
        this.userService = userService;
        this.enhancer = enhancer;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2Request request = authentication.getOAuth2Request();
        if (!request.getScope().contains(Scopes.ID_SCOPE))
            return accessToken;

        if (isAuthorizationCodeRequest(request) || (implicitEnabled && isImplicitRequest(request)))
            return appendIdToken(accessToken, authentication);

        return accessToken; // return normal token for other grant types
    }

    /**
     * This method uses an access token to generate an ID token.
     * some claims are taken directly from the access toke and mapped to the ID token
     * <p>
     * The ID token is generated with base claims, then depending on the scopes requested
     * a delegate {@link IDTokenClaimsEnhancer} populates the required claims
     *
     * @param accessToken    access token
     * @param authentication authentication context containing the authentication request
     * @return IDToken
     */
    private OAuth2AccessToken appendIdToken(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2Request request = authentication.getOAuth2Request();

        String nonce = request.getRequestParameters().get(NONCE);
        Claims accessTokenClaims = new DefaultClaims(super.decode(accessToken.getValue()));
        accessTokenClaims.put(AUDIENCE, request.getClientId());

        OidcIdToken.Builder oidcIdTokenBuilder = OidcIdToken.withTokenValue(accessToken.getValue())
                .issuer(accessTokenClaims.getIssuer())
                .subject(accessTokenClaims.getSubject())
                .audience(Set.of(accessTokenClaims.getAudience()))
                .authorizedParty(request.getClientId())
                .nonce(nonce)
                .expiresAt(accessTokenClaims.getExpiration().toInstant())
                .accessTokenHash(generateAccessTokenHash(accessToken))
                .authorizationCodeHash(generateCodeHash(accessToken, authentication))
                .authTime(accessTokenClaims.getIssuedAt().toInstant())
                .issuedAt(Instant.now())
                .authenticationMethods(getAuthenticationMethods(authentication));

        String username = accessTokenClaims.getSubject();
        User user = userService.findUserByUsername(username);

        OidcUserInfo.Builder userInfoBuilder = OidcUserInfo.builder();

        if (request.getScope().contains(Scopes.IDTokenScopes.PROFILE))
            oidcIdTokenBuilder.claims(claimsMap -> enhancer.addProfileClaims(userInfoBuilder, user));


        if (request.getScope().contains(Scopes.IDTokenScopes.EMAIL))
            oidcIdTokenBuilder.claims(claimsMap -> enhancer.addEmailClaims(userInfoBuilder, user));

        OidcUserInfo oidcUserInfo = userInfoBuilder.build();
        oidcIdTokenBuilder.claims(claims -> claims.putAll(oidcUserInfo.getClaims()));

        OidcIdToken oidcIdToken = oidcIdTokenBuilder.build();
        IDToken idToken = new IDToken(oidcIdToken);

        String idTokenString = super.encode(idToken, authentication);

        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        token.setAdditionalInformation(Map.of(IDToken.TYPE, idTokenString));

        return token;
    }

    // generates the at_hash
    protected String generateAccessTokenHash(OAuth2AccessToken accessToken) {

        String algorithm = getHashAlgorithmForToken(accessToken.getValue());
        MessageDigest MD5 = getMessageDigestInstance(algorithm);
        // - get ascii representation of the token
        byte[] asciiValues = accessToken.getValue().getBytes(StandardCharsets.US_ASCII);

        // - hash the ascii value using the jwt hashing algorithm
        byte[] hashedToken = MD5.digest(asciiValues);

        // get the first 128 bits (hash alg length / 2 === 256 / 2)
        byte[] bytes = Arrays.copyOf(hashedToken, 16);

        return Base64.getEncoder().encodeToString(bytes);
    }

    // generate the c_hash claim value
    protected String generateCodeHash(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        OAuth2Request request = authentication.getOAuth2Request();
        Map<String, String> requestParameters = request.getRequestParameters();
        String authorizationCode = requestParameters.get("code");
        if (authorizationCode == null) return null;

        String algorithm = getHashAlgorithmForToken(accessToken.getValue());
        byte[] hashedCode = hashString(algorithm, authorizationCode);
        byte[] bytes = Arrays.copyOf(hashedCode, 16);

        return Base64.getEncoder().encodeToString(bytes);
    }

    // you should override this
    // RS256 is used to sign tokens so the algorithm returns SHA-256
    protected String getHashAlgorithmForToken(String token) {
        Map<String, String> headers = JwtHelper.headers(token);
        String tokenAlg = headers.get("alg");
        return "SHA-".concat(tokenAlg.substring(2));
    }

    protected List<String> getAuthenticationMethods(Authentication authentication) {
        return List.of("user");
    }
}
