package com.benoly.auth.controller.registration.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */

@Data
@Builder
public class UserDto {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
}
