package dev.rexijie.auth.tokenservices;

import dev.rexijie.auth.model.token.KeyPairHolder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.IssuerClaimVerifier;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtClaimsSetVerifier;

import java.net.URL;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Map;

public class JwtTokenEnhancer extends JwtAccessTokenConverter {
    @Value("${oauth2.openid.discovery.issuer:https://rexijie.dev}")
    private String issuer;
    private final JsonParser objectMapper = JsonParserFactory.create();
    private final Signer signer;
    private final KeyPairHolder keyPairHolder;

    public JwtTokenEnhancer(KeyPairHolder keyPairHolder) {
        super();
        this.keyPairHolder = keyPairHolder;
        setKeyPair(keyPairHolder.getKeyPair());
        this.signer = new RsaSigner((RSAPrivateKey) keyPairHolder.getPrivateKey());
    }

    @SneakyThrows
    @Override
    public void setJwtClaimsSetVerifier(JwtClaimsSetVerifier jwtClaimsSetVerifier) {
        var issuerUrl = new URL(issuer);
        super.setJwtClaimsSetVerifier(new IssuerClaimVerifier(issuerUrl));
    }

    @Override
    public void setKeyPair(KeyPair keyPair) {
        super.setKeyPair(keyPair);
        this.setVerifierKey(generateRSAString(keyPair));
    }

    protected String generateRSAString(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return "-----BEGIN PUBLIC KEY-----"
                + Base64.getEncoder().encodeToString(publicKey.getEncoded())
                + "-----END PUBLIC KEY-----";
    }

    @Override
    protected String encode(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        String content;
        final Map<String, String> customHeaders;
        try {
            content = objectMapper.formatMap(
                    getAccessTokenConverter()
                            .convertAccessToken(accessToken, authentication));
        } catch (Exception e) {
            throw new IllegalStateException("Cannot convert access token to JSON", e);
        }
        return JwtHelper.encode(
                content,
                signer,
                getCustomHeaders())
                .getEncoded();
    }

    protected Map<String, String> getCustomHeaders() {
        return Map.of("kid", keyPairHolder.getId());
    }
}
