package dev.rexijie.auth.errors;

/**
 * Exception thrown when an attempt is made to assign a duplicate
 * username, user identifier or subject claim
 *
 * @author Rex Ijiekhuamen
 */
public class UserExistsException extends RuntimeException {
    public UserExistsException(String message) {
        super(message);
    }
}
