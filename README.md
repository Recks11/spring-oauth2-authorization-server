# Spring OAuth2 Authorization Server
This is an OAuth2 authorization server written with Spring Boot capable of generating and granting JWTs. All flows are stateless except the `authorization_code` flow


## Endpoints
The baseUrl is `http://localhost:8000/**`.

There are 4 endpoints
- `/oauth/token` to get tokens with the password, implicit, client_credentials and refresh_token flows.
- `/oauth/authorize` for the authorization_code flow.
- `/oauth/check_token` to check the tokens with your resource server.
- `/oauth/token_key` to get the public key used to verify tokens.
