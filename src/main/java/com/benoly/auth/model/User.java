package com.benoly.auth.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@Document
@NoArgsConstructor
public class User implements UserDetails {
    private static final long serialVersionUID = 8668310170868956407L;
    @Id
    private String id;
    private String username;
    private String password;
    private Role role;
    // private UserInfo additionalUserInfo;
    private boolean isEnabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return new ArrayList<>(role.getAuthorities());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("User").append(" {");
        sb.append("Username: ").append(this.username).append("; ");
        sb.append("Password: [PROTECTED]; ");
        sb.append("Role: ").append(this.role).append("; ");
        sb.append("Enabled: ").append(this.isEnabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
                .append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        sb.append(" }");

        return sb.toString();
    }
}
