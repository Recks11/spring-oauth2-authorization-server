package dev.rexijie.auth.controller.registration.user;

import dev.rexijie.auth.controller.registration.dto.UserDto;
import dev.rexijie.auth.controller.registration.dto.mapper.UserMapper;
import dev.rexijie.auth.model.OidcAddress;
import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.UserInfo;
import dev.rexijie.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Rex Ijiekhuamen
 * 09 Sep 2020
 */
@RequestMapping("/api/user")
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
    public ResponseEntity<User> updateUser(@RequestBody UserInfo userInfo,
                                           @PathVariable("id") String id) {
        validateUserInfo(userInfo);
        User user = userService.getUserById(id);
        user.setUserInfo(userInfo);
        userService.updateUserInfo(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/{id}/address")
    public ResponseEntity<User> updateUserAddress(@RequestBody OidcAddress address,
                                                  @PathVariable("id") String id) {
        validateAddress(address);
        User user = userService.getUserById(id);
        user.getUserInfo().setAddress(address);

        User updatedUser = userService.updateUserInfo(user);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    private void validateUser(UserDto userDto) {

    }
    private void validateUserInfo(UserInfo userInfo) {

    }

    private void validateAddress(OidcAddress address) {

    }
}
