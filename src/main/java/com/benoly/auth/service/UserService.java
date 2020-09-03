package com.benoly.auth.service;

import com.benoly.auth.model.User;
import com.benoly.auth.model.UserProfile;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User findUserByUsername(String username);
    UserProfile findProfileByUserId(String id);
    UserProfile findProfileByUsername();
}
