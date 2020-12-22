package dev.rexijie.auth.init;

import dev.rexijie.auth.constants.GrantTypes;
import dev.rexijie.auth.model.User;
import dev.rexijie.auth.model.UserInfo;
import dev.rexijie.auth.model.authority.Authority;
import dev.rexijie.auth.model.authority.Role;
import dev.rexijie.auth.model.authority.RoleEnum;
import dev.rexijie.auth.model.client.Client;
import dev.rexijie.auth.model.client.ClientProfiles;
import dev.rexijie.auth.model.client.ClientTypes;
import dev.rexijie.auth.repository.ClientRepository;
import dev.rexijie.auth.repository.RoleRepository;
import dev.rexijie.auth.repository.UserRepository;
import dev.rexijie.auth.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author Rex Ijiekhuamen
 * 08 Sep 2020
 */
@Slf4j
@Component
public class Bootstrap implements ApplicationListener<ApplicationStartedEvent> {
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final ClientService clientService;

    public Bootstrap(UserRepository userRepository,
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

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        log.info("*********************************************");
        log.info("*         INITIALISING TEST DATA            *");
        log.info("*********************************************");
        userRepository.deleteAll();
        clientRepository.deleteAll();
        roleRepository.deleteAll();
        List<Role> roles = createRoles();
        var userRole = getRoleFromEnum(roles, RoleEnum.USER);
        createClient();
        createUser(userRole);
        log.info("*********************************************");
        log.info("*        <(0_0<)   DONE   (>0_0)>           *");
        log.info("*********************************************");
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
        var registeredClient = new Client("Benoly management app", ClientTypes.CONFIDENTIAL, ClientProfiles.WEB);
        registeredClient.setClientId("management-app");
        registeredClient.setClientSecret(encoder.encode("secret"));
        registeredClient.setAccessTokenValiditySeconds(10 * 60);
        registeredClient.setRefreshTokenValiditySeconds(15 * 60);
        registeredClient.setResourceIds(List.of("stock-api"));
        registeredClient.setScope(List.of("read", "read:appointments", "write", "remove", "profile", "openid", "email"));
        registeredClient.setRegisteredRedirectUri(Set.of("http://localhost:8008/login/oauth2/code/",
                "http://localhost:3000/"));
        registeredClient.setAuthorizedGrantTypes(
                List.of(GrantTypes.REFRESH_TOKEN, GrantTypes.PASSWORD, GrantTypes.AUTHORIZATION_CODE, GrantTypes.IMPLICIT));

        Client save = clientService.addClient(registeredClient);
        log.info("added client {}", save.toString());
    }

    private void createUser(Role role) {
        var user = new User("rexijie@gmail.com", encoder.encode("pass@rex"), role);
        var profile = UserInfo.builder()
                .firstName("Rex")
                .lastName("Ijiekhuamen")
                .username(user.getUsername())
                .email(user.getUsername())
                .dateOfBirth(LocalDate.of(2000, 1, 30)) // random day
                .build();
        user.setId(generateId());
        user.setEnabled(true);
        user.setUserInfo(profile);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setCreatedAt(LocalDateTime.now());
        User save = userRepository.save(user);
        log.info("added user {}", save);
    }

    private void logAllData() {
        userRepository.findAll()
                .forEach(user -> log.info("Added User {}", user));
        clientRepository.findAll()
                .forEach(client -> log.info("Added client {}", client));
        roleRepository.findAll()
                .forEach(role -> log.info("Added role {}", role));
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
