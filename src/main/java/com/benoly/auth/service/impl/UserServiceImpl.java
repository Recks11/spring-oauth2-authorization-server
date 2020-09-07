package com.benoly.auth.service.impl;

import com.benoly.auth.errors.UserExistsException;
import com.benoly.auth.model.Authority;
import com.benoly.auth.model.User;
import com.benoly.auth.model.UserInfo;
import com.benoly.auth.repository.UserRepository;
import com.benoly.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.benoly.auth.util.ObjectUtils.applyIfNonNull;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    @Override
    public User findUserByUsername(String username) {
        return (User) loadUserByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var user = userRepository.findByUsername(username);
        if (user == null) throw new UsernameNotFoundException("User does not exist");
        user.getRole().getAuthorities().add(new Authority(user.getRole().getName(), user.getRole().getDescription()));
        return user;
    }

    @Override
    public UserInfo findProfileByUserId(String id) {
        return null;
    }

    @Override
    public UserInfo findProfileByUsername(String username) {
        return findUserByUsername(username).getUserInfo();
    }

    @Override
    public User addUser(User user) {
        User storedUser = findUserByUsername(user.getUsername());
        if (storedUser != null) throw new UserExistsException("A user with the username exists");

        String id = UUID.fromString(user.getUsername()).toString();
        user.setId(id);
        user.setCreatedAt(LocalDateTime.now());
        return save(user);
    }

    @Override
    public User updateUserInfo(User user) {
        User storedUser = findUserByUsername(user.getUsername());
        if (storedUser == null) return null;

        UserInfo sentUserInfo = user.getUserInfo();
        UserInfo storedInfo = storedUser.getUserInfo();

        applyIfNonNull(sentUserInfo.getUsername(), storedInfo::setUsername);
        applyIfNonNull(sentUserInfo.getFirstName(), storedInfo::setFirstName);
        applyIfNonNull(sentUserInfo.getLastName(), storedInfo::setLastName);
        applyIfNonNull(sentUserInfo.getEmail(), storedInfo::setEmail);
        applyIfNonNull(sentUserInfo.getDataOfBirth(), storedInfo::setDataOfBirth);
        return update(storedUser);
    }

    protected User updatePassword(String username, String rawPassword) {
        User userToUpdate = findUserByUsername(username);
        String encryptedPassword = encoder.encode(rawPassword);
//        if (encryptedPassword.equals(userToUpdate.getPassword()))
        userToUpdate.setPassword(encryptedPassword);
        return update(userToUpdate);
    }

    protected User disableUser(String username) {
        User user = findUserByUsername(username);
        user.setAccountNonLocked(false);
        return update(user);
    }

    protected User update(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    protected User save(User user) {
        return userRepository.save(user);
    }
}
