package com.benoly.auth;

import com.benoly.auth.model.*;
import com.benoly.auth.repository.ClientRepository;
import com.benoly.auth.repository.RoleRepository;
import com.benoly.auth.repository.UserRepository;
import com.benoly.auth.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.benoly.auth.constants.GrantTypes.*;

@Slf4j
@SpringBootApplication
public class Oauth2ServerApplication implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ClientService clientService;

    public Oauth2ServerApplication(UserRepository userRepository,
                                   ClientRepository clientRepository,
                                   RoleRepository roleRepository,
                                   PasswordEncoder encoder,
                                   ClientService clientService) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.clientService = clientService;
    }

    public static void main(String[] args) {
        SpringApplication.run(Oauth2ServerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        log.info("clearing repository");
        userRepository.deleteAll();
        clientRepository.deleteAll();
        roleRepository.deleteAll();
        List<Role> roles = createRoles();
        var userRole = getRoleFromEnum(roles, RoleEnum.USER);
        createClient();
        createUser(userRole);

        userRepository.findAll().forEach(user -> log.info("found user {}", user));
        clientRepository.findAll().forEach(client -> log.info("found client {}", client));
        roleRepository.findAll().forEach(role -> log.info("found role {}", role));
    }

    private List<Role> createRoles() {
        var authority = new Authority();
        authority.setId(generateId());
        authority.setName("CAN_VIEW");
        authority.setDescription("user can view stuff");

        var userRole = new Role(RoleEnum.USER);
        userRole.setId(generateId());
        userRole.getAuthorities().add(authority);

        var adminRole = new Role(RoleEnum.ADMIN);
        adminRole.setId(generateId());
        roleRepository.saveAll(List.of(userRole, adminRole));

        return List.of(userRole, adminRole);
    }

    private void createClient() {
        var defaultClient = new Client();
        defaultClient.setId(generateId());
        defaultClient.setName("Benoly management app");
        defaultClient.setClientId("management-app");
        defaultClient.setClientSecret(encoder.encode("secret"));
        defaultClient.setAccessTokenValiditySeconds(10 * 60);
        defaultClient.setRefreshTokenValiditySeconds(15 * 60);
        defaultClient.setScope(List.of("read", "write", "remove"));
        defaultClient.setRegisteredRedirectUri(Set.of("http://localhost:8000/api/me"));
        defaultClient.setAuthorizedGrantTypes(List.of(REFRESH_TOKEN, PASSWORD, AUTHORIZATION_CODE));

        Client save = clientService.addClient(defaultClient);
        log.info("added client {}", save.toString());
    }

    private void createUser(Role role) {
        var user = new User("rexijie@gmail.com", encoder.encode("pass@rex"), role);
        user.setId(generateId());
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        User save = userRepository.save(user);
        log.info("added user {}", save);
    }

    private Role getRoleFromEnum(List<Role> roles, RoleEnum roleEnum) {
        return roles.stream().filter(
                role -> role.getName().equals(roleEnum.getName())
        ).findFirst().orElse(new Role(RoleEnum.USER));
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
