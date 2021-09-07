package dev.rexijie.auth.controller.registration.dto;

import dev.rexijie.auth.model.OidcAddress;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

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
    private OidcAddress address;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String pictureUrl;
}
