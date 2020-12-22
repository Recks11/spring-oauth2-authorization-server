package dev.rexijie.auth.controller.registration.dto.mapper;

import dev.rexijie.auth.controller.registration.dto.UserDto;
import dev.rexijie.auth.model.User;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
public class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .username(user.getUsername())
                .password("[REDACTED]")
                .address(user.getUserInfo().getAddress())
                .email(user.getUserInfo().getEmail())
                .firstName(user.getUserInfo().getFirstName())
                .lastName(user.getUserInfo().getLastName())
                .pictureUrl(user.getUserInfo().getPictureUrl())
                .dateOfBirth(user.getUserInfo().getDateOfBirth())
                .phone(user.getUserInfo().getPhoneNumber())
                .build();
    }

    public static User toUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername() != null ? userDto.getUsername() : userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.getUserInfo().setFirstName(userDto.getFirstName());
        user.getUserInfo().setLastName(userDto.getLastName());
        user.getUserInfo().setEmail(userDto.getEmail());
        user.getUserInfo().setPhoneNumber(userDto.getPhone());
        user.getUserInfo().setDateOfBirth(userDto.getDateOfBirth());
        user.getUserInfo().setPictureUrl(userDto.getPictureUrl());
        user.getUserInfo().setAddress(userDto.getAddress());
        return user;
    }
}
