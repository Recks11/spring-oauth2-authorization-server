package dev.rexijie.auth.model.token;

import dev.rexijie.auth.model.Entity;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationToken extends Entity {
    private byte[] authentication;
    private String username;
    private String code;
    private boolean used;
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return expiresAt.isBefore(LocalDateTime.now());
    }
}
