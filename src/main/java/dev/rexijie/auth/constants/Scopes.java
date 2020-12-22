package dev.rexijie.auth.constants;

public class Scopes {
    public static final String READ_SCOPE = "read";
    public static final String WRITE_SCOPE = "write";
    public static final String ID_SCOPE = "openid";

    public static class IDTokenScopes {
        public static final String PROFILE = "profile";
        public static final String EMAIL = "email";
        public static final String ADDRESS = "address";
        public static final String PHONE = "phone";
    }
}
