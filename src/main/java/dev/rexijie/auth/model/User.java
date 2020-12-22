package dev.rexijie.auth.model;

import dev.rexijie.auth.model.authority.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Getter
@Setter
@Document
@NoArgsConstructor
public class User extends Entity implements UserDetails {
    private static final long serialVersionUID = 8668310170868956407L;
    private String username;
    private String password;
    private Role role;
    private transient UserInfo userInfo;
    private boolean isEnabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

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
        sb.append("Profile: [PROTECTED]; ");
        sb.append("Role: ").append(this.role).append("; ");
        sb.append("Enabled: ").append(this.isEnabled).append("; ");
        sb.append("AccountNonExpired: ").append(this.accountNonExpired).append("; ");
        sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
                .append("; ");
        sb.append("AccountNonLocked: ").append(this.accountNonLocked).append("; ");

        sb.append(" }");

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return username.equals(user.username) &&
                role.equals(user.role) &&
                Objects.equals(userInfo, user.userInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, role, userInfo);
    }
}
