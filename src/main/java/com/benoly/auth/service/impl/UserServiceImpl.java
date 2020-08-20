package com.benoly.auth.service.impl;

import com.benoly.auth.model.Authority;
import com.benoly.auth.repository.UserRepository;
import com.benoly.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userRepository.findByUsername(username);
        user.getRole().getAuthorities().add(new Authority(user.getRole().getName(), user.getRole().getDescription()));
        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRole().getAuthorities())
                .accountLocked(!user.isEnabled())
                .disabled(!user.isEnabled())
                .build();
    }
}
