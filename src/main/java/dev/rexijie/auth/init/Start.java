package dev.rexijie.auth.init;

import dev.rexijie.auth.constants.GrantTypes;
import dev.rexijie.auth.model.client.Client;
import dev.rexijie.auth.model.client.ClientProfiles;
import dev.rexijie.auth.model.client.ClientTypes;
import dev.rexijie.auth.repository.ClientRepository;
import dev.rexijie.auth.service.ClientService;
import dev.rexijie.auth.service.SecretGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * @author Rex Ijiekhuamen
 * 22 Dec 2020
 */
@Slf4j
@Component
@Profile({"docker", "dev"})
public class Start implements ApplicationListener<ApplicationStartedEvent> {
    @Value("${oauth2.server.resourceid}")
    private String resourceId;
    @Value("${oauth2.openid.discovery.baseUri}")
    private String baseUrl;
    private final String defaultClientName = "X9125 Authorization Server";
    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;
    private final SecretGenerator generator;
    private final ClientService clientService;

    public Start(ClientRepository clientRepository,
                 PasswordEncoder encoder,
                 SecretGenerator generator, ClientService clientService) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
        this.generator = generator;
        this.clientService = clientService;
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        var client = clientRepository.findClientByClientName(defaultClientName);
        if (client == null) createClient();
    }

    private void createClient() {
        String clientId = generator.generate(16);
        String clientSecret = generator.generate(64);

        var registeredClient = new Client(defaultClientName, ClientTypes.CONFIDENTIAL, ClientProfiles.WEB);
        registeredClient.setClientId(clientId);
        registeredClient.setClientSecret(encoder.encode(clientSecret));
        registeredClient.setAccessTokenValiditySeconds(10 * 60);
        registeredClient.setRefreshTokenValiditySeconds(15 * 60);
        registeredClient.setResourceIds(List.of(resourceId));
        registeredClient.setScope(List.of("read", "write", "openid", "profile", "email"));
        registeredClient.setRegisteredRedirectUri(
                Set.of(baseUrl + "/login/oauth2/code/",
                        baseUrl));
        registeredClient.setAuthorizedGrantTypes(
                List.of(GrantTypes.REFRESH_TOKEN, GrantTypes.PASSWORD, GrantTypes.AUTHORIZATION_CODE, GrantTypes.IMPLICIT));

        clientService.addClient(registeredClient);
        log.info("created default client");
        log.info("Client ID: {}", clientId);
        log.info("Client Secret: {}", clientSecret);
    }
}
