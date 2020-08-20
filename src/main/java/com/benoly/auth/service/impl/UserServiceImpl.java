package com.benoly.auth.service.impl;

import com.benoly.auth.model.Authority;
import com.benoly.auth.repository.UserRepository;
import com.benoly.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
        if (user == null) throw new UsernameNotFoundException("User does not exist");
        user.getRole().getAuthorities().add(new Authority(user.getRole().getName(), user.getRole().getDescription()));
        return user;
    }
}
