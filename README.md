# Spring OAuth2 Authorization Server
This is an OAuth2 authorization server written with Spring Boot capable of generating and granting JWTs. All flows are stateless except the `authorization_code` flow


## Endpoints
The baseUrl is `http://localhost:8000/**`.

There are 4 endpoints
- `/oauth/token` to get tokens with the password, implicit, client_credentials and refresh_token flows.
- `/oauth/authorize` for the authorization_code flow.
- `/oauth/check_token` to check the tokens with your resource server.
- `/oauth/token_key` to get the public key used to verify tokens.

## FLOWS
This section describes how to use the various OAuth2 flows.

JWTs produced by this application are encrypted using the RSA256 algorithm and hence are signed with a key pair. The public key can be gotten from the `/oauth/token_key` endpoint.

```json
{
    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXhpamllQGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwic2NvcGUiOlsicmVhZCJdLCJpc3MiOiJodHRwczovL3JleGlqaWUuZGV2IiwiZXhwIjoxNTk4MDE0NTcyLCJhdXRob3JpdGllcyI6WyJST0xFX0NBTl9WSUVXIiwiUk9MRV9VU0VSIl0sImp0aSI6IjViMzA1YTE4LWQ0NWMtNDA4YS1iNGU3LWEzYmYwODQ3NWE4ZCIsImNsaWVudF9pZCI6Im1hbmFnZW1lbnQtYXBwIn0.li0f2gEA2VsbginzWa0ELcKrWGXeXSybsZVFdQiWHRZ2YbqvuYbpr0ReN_D6_0zWgCBdWjblibSLUiLrM2vlQBr0UarU1RnaDP5WDTxnTBch80rjWIfc-_QBwFOuitD7iXHwRhJLDObv491YcxLcmXhJmPTr-CavgG-cruD6kuqIzqpwQ22-TXZ_iHT2OCddsSX-DUtXMIb7oBIkbUgdc3UCmFn2fdVsFxZbUM2CYsKc56VgGO27MlfKfRQhCfIhBIzpvXmBRUETWMipOJOCtJ60JPW1NM78-lgV-Y8lw280SZAgK5jukJNshNXJgkqw42scQMSdXJTKg-WBWoV6Bg",
    "token_type": "bearer",
    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJyZXhpamllQGdtYWlsLmNvbSIsInJvbGUiOiJVU0VSIiwic2NvcGUiOlsicmVhZCJdLCJhdGkiOiI1YjMwNWExOC1kNDVjLTQwOGEtYjRlNy1hM2JmMDg0NzVhOGQiLCJpc3MiOiJodHRwczovL3JleGlqaWUuZGV2IiwiZXhwIjoxNjAwNTYzMzcyLCJhdXRob3JpdGllcyI6WyJST0xFX0NBTl9WSUVXIiwiUk9MRV9VU0VSIl0sImp0aSI6IjE1YmNjYjQwLTQ3NWEtNDk4My05YWI2LTczNmZhNmI2MDU5OSIsImNsaWVudF9pZCI6Im1hbmFnZW1lbnQtYXBwIn0.P8tW6DsEd1qefdWMGZiBq7hlaYSl6hFZ2aRACHf5u-F-NUTY7F9wiB1vXRoDFS577AwRAajPFB5Mq-IFsGl4LfOoth9AjJJpA9EF3hPXj6XH6f49Ozzn2mF8AvEZBO-SJ04eK1eS-cJN03YK4FBTO9LT59-6SLqzhGE8x-NwGQWSab91Gv7_DmmuPHEM_vAnQfBV9ycuN0wdcJmaj1wsRnbBAtCe-bETu9LZgQ5vw5ANCd8Dfz0DTM2vu6vCFTpFeFwMy91Ol73POh34z_pGd2tgSaWzJm_qCVq-hKOjXj-4d2tmDvLcwUzPtwCvbUrbPoQYyF9RZEO8NOdr0--3IA",
    "expires_in": 43199,
    "scope": "read",
    "jti": "5b305a18-d45c-408a-b4e7-a3bf08475a8d"
}
```
### PASSWORD FLOW
...
### IMPLICIT FLOW
...
### CLIENT CREDENTIALS FLOW
...
### REFRESH TOKEN FLOW
...
### AUTHORIZATION CODE FLOW
...

