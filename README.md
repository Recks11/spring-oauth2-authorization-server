# Spring OAuth2 Authorization Server
This is an OAuth2 authorization server written with Spring Boot capable of generating and granting JWTs. All flows are stateless except the `authorization_code` flow


## Endpoints
The baseUrl is `http://127.0.0.1:8080/**`. but you should provide yours using the `${SERVER_URL}` environment variable.

There are 4 endpoints
- `/oauth2/token` to get tokens with the password, implicit, client_credentials and refresh_token flows.
- `/oauth2/authorize` for the authorization_code flow.
- `/oauth2/check_token` to check the tokens with your resource server.
- `/oauth2/token_key` to get the public key used to verify tokens.
- `/oauth2/introspect` the introspection endpoint
- `/openid/userinfo` user info endpoint
- `/openid/.well-known/jwks.json` openid jwks_uri 
- `/openid/.well-known/openid-configuration` openid discovery endpoint


## USAGE
This authorization server supports openid discovery which enables it take advantage of spring-security-oauth2 openid configuration

### Configuring a RESOURCE SERVER
Configuring a resource server app to use this authorization server is as easy as setting the issuer-uri property in the application.properties or application.yml file
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://127.0.0.1:8080/openid
```
You can then configure security in your WebSecurityConfigurerAdapter class. the Jwt decoder Bean gets its configuration from the authorization server.


```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                                .decoder(jwtDecoder()))
                );
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        CustomAuthenticationConverter grantedAuthoritiesConverter = new CustomAuthenticationConverter();
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation(issuerUri);
    }
}
```
The `jwtAuthenticationConverter()` is optional, but you can add it if you want to customise the granted authorities in the generated jwt.

### Configuring a CLIENT
configuring a `Spring-security-oauth2-client` to use the authorization server you need to provide the issuer-uri property. 
```yaml
spring:
  security:
    oauth2:
      client:
        provider:
          rexijie-dev:
            issuerUri: http://127.0.0.1:8080/openid
```
and then the security configuration
```java
@EnableWebFluxSecurity
public class OAuth2Config {

    @Value("${spring.security.oauth2.client.provider.rexijie-dev.issuerUri}")
    private String issuerUri;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(
                        authorize -> authorize
                                .pathMatchers("/").permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2Client(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults());

        return http.build();
    }
}
```