package com.benoly.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Role extends Identified {
    private String name;
    private String description;
    private List<Authority> authorities = new ArrayList<>();

    public Role(RoleEnum roleEnum) {
        this.name = roleEnum.getName();
        this.description = roleEnum.getDescription();
    }
}
