package com.benoly.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import static com.benoly.auth.config.WebSecurityConfig.ROLE_PREFIX;

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
        return ROLE_PREFIX + name;
    }
}
