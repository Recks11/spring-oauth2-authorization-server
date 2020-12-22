package dev.rexijie.auth.controller.registration.user;

import dev.rexijie.auth.controller.registration.dto.UserDto;
import dev.rexijie.auth.controller.registration.dto.mapper.UserMapper;
import dev.rexijie.auth.model.OidcAddress;
import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.UserInfo;
import dev.rexijie.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
@RestController
@RequestMapping("/api/users")
public class UserRegistrationEndpoint {
    private final UserService userService;

    public UserRegistrationEndpoint(UserService userService) {
        this.userService = userService;
    }

    // do i add userinfo request types?
    // so UserUpdateRequest
    // UserDeleteRequest
    // UserCreationRequest
    // UserDeleteRequest

    @GetMapping("/principal")
    public ResponseEntity<Object> getPrincipal(@AuthenticationPrincipal Authentication authentication) {
        return new ResponseEntity<>(authentication, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody UserDto userDto) {
        validateUser(userDto);
        User user = UserMapper.toUser(userDto);

        User savedUser = userService.addUser(user);
        return new ResponseEntity<>(savedUser, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        User user = userService.getUserById(id);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserInfo userInfo,
                                           @PathVariable("id") String id) {
        validateUserInfo(userInfo);
        User user = userService.getUserById(id);
        user.setUserInfo(userInfo);
        User updatedUser = userService.updateUserInfo(user);
        UserDto userDto = UserMapper.toDto(updatedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping("/{id}/address")
    public ResponseEntity<UserDto> updateUserAddress(@RequestBody OidcAddress address,
                                                  @PathVariable("id") String id) {
        validateAddress(address);
        User user = userService.getUserById(id);
        user.getUserInfo().setAddress(address);

        User updatedUser = userService.updateUserInfo(user);

        UserDto userDto = UserMapper.toDto(updatedUser);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    private void validateUser(UserDto userDto) {
        // TODO impliment
    }
    private void validateUserInfo(UserInfo userInfo) {
        // TODO impliment
    }

    private void validateAddress(OidcAddress address) {
        // TODO impliment
    }
}
