package com.benoly.auth.model.token;

import com.benoly.auth.model.Entity;
import lombok.*;

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
}
