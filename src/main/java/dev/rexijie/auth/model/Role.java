package dev.rexijie.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends Identified implements Serializable {
    private static final long serialVersionUID = 1373828140005067324L;
    private String name;
    private String description;
    private Set<Authority> authorities = new HashSet<>();

    public Role(RoleEnum roleEnum) {
        this.name = roleEnum.getName();
        this.description = roleEnum.getDescription();
    }

    public Role(RoleEnum roleEnum, Collection<Authority> authorities) {
        this(roleEnum);
        this.authorities = Set.copyOf(authorities);
    }
}
