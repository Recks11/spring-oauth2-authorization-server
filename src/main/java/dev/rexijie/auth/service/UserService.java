package dev.rexijie.auth.service;

import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findUserByUsername(String username);
    UserInfo findProfileByUserId(String id);
    UserInfo findProfileByUsername(String username);
    User addUser(User user);
    User getUserById(String id);
    User updateUserInfo(User user);
}
