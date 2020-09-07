package com.benoly.auth.constants;

public class Claims {
    public static class JwtClaims {
        public static final String USERNAME_CLAIM = "user_name";
        public static final String ROLE_CLAIM = "role";
    }
    public static class OpenIdClaims {
        public static final String NAME_CLAIM = "name";
        public static final String FAMILY_NAME_CLAIM = "family_name";
        public static final String GIVEN_NAME_CLAIM = "given_name";
        public static final String PREFERRED_USERNAME_CLAIM = "preferred_username";
        public static final String BIRTH_DATE_CLAIM = "birthdate";
        public static final String EMAIL_CLAIM = "email";
        public static final String EMAIL_VERIFIED = "email_verified";
        public static final String PHONE_CLAIM = "phone";
        public static final String PHONE_VERIFIED = "phone_verified";
        public static final String PICTURE_CLAIM = "picture";
        public static final String PROFILE_CLAIM = "profile";
        public static final String AUTHORIZED_PARTY = "azp";
        public static final String AUTH_TIME = "auth_time";
        public static final String NONCE = "nonce";
        public static final String ACCESS_TOKEN_HASH = "at_hash";

    }
}
