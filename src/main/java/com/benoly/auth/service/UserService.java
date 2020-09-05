package com.benoly.auth.service;

import com.benoly.auth.model.User;
import com.benoly.auth.model.UserInfo;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findUserByUsername(String username);
    UserInfo findProfileByUserId(String id);
    UserInfo findProfileByUsername(String username);
}
