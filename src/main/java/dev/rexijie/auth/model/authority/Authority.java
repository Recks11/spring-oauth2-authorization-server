package dev.rexijie.auth.model.authority;

import dev.rexijie.auth.constants.Authorities;
import dev.rexijie.auth.model.Identified;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Authority extends Identified implements GrantedAuthority {
    private String name;
    private String description;

    public Authority(AuthorityEnum authorityEnum) {
        this.name = authorityEnum.getName();
        this.description = authorityEnum.getDescription();
    }

    @Override
    public String getAuthority() {
        return Authorities.ROLE_PREFIX + name;
    }
}
