package dev.rexijie.auth.service.impl;

import dev.rexijie.auth.errors.UserExistsException;
import dev.rexijie.auth.model.Authority;
import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.UserInfo;
import dev.rexijie.auth.repository.UserRepository;
import dev.rexijie.auth.service.UserService;
import dev.rexijie.auth.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

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
        user.setPassword(encoder.encode(user.getPassword()));
        return save(user);
    }

    @Override
    public User getUserById(String id) {

        Optional<User> userOp = userRepository.findById(id);
        if (userOp.isEmpty()) throw new UsernameNotFoundException("user does not exist");
            User user = userOp.get();
            user.getRole()
                    .getAuthorities()
                    .add(new Authority(user.getRole().getName(), user.getRole().getDescription()));

            return user;
    }

    @Override
    public User updateUserInfo(User user) {
        User storedUser = findUserByUsername(user.getUsername());
        if (storedUser == null) return null;

        UserInfo sentUserInfo = user.getUserInfo();
        UserInfo storedInfo = storedUser.getUserInfo();

        ObjectUtils.applyIfNonNull(sentUserInfo.getUsername(), storedInfo::setUsername);
        ObjectUtils.applyIfNonNull(sentUserInfo.getFirstName(), storedInfo::setFirstName);
        ObjectUtils.applyIfNonNull(sentUserInfo.getLastName(), storedInfo::setLastName);
        ObjectUtils.applyIfNonNull(sentUserInfo.getEmail(), storedInfo::setEmail);
        ObjectUtils.applyIfNonNull(sentUserInfo.getDateOfBirth(), storedInfo::setDateOfBirth);
        ObjectUtils.applyIfNonNull(sentUserInfo.getAddress(), storedInfo::setAddress);
        return update(storedUser);
    }

    protected User updatePassword(String username, String rawPassword) {
        User userToUpdate = findUserByUsername(username);
//        String encryptedPassword = encoder.encode(rawPassword);
//        if (encryptedPassword.equals(userToUpdate.getPassword()))
        userToUpdate.setPassword(rawPassword);
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
