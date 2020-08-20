package com.benoly.auth.service.impl;

import com.benoly.auth.model.Authority;
import com.benoly.auth.model.AuthorityEnum;
import com.benoly.auth.model.Client;
import com.benoly.auth.repository.ClientRepository;
import com.benoly.auth.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.benoly.auth.constants.GrantTypes.PASSWORD;
import static com.benoly.auth.util.ObjectUtils.applyIfNonNull;
import static com.benoly.auth.util.TokenUtils.generateUUID;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;

    public ClientServiceImpl(ClientRepository clientRepository,
                             PasswordEncoder encoder) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
    }

    @Override
    public Client addClient(Client client) {
        var defaultClient = createDefaultClient();
        assignNonEmptyFields(client, defaultClient);

        defaultClient.addAuthority(createClientAuthority());
        return clientRepository.save(defaultClient);
    }

    public Client findByClientId(String clientId) {
        Client foundClient = clientRepository.findByClientId(clientId);
        if (foundClient == null) throw new NoSuchClientException("client with id " + clientId + "not found");
        return foundClient;
    }

    @Override
    @Cacheable(value = "registered-clients", key = "#root.args[0]")
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        Client found;
        try {
            found = clientRepository.findByClientId(clientId);
        } catch (NoSuchClientException e) {
            throw new ClientRegistrationException("Client has not been registered");
        }
        return found;
    }

    @Override
    @CacheEvict(value = "registered-clients", key = "#root.args[0]")
    public Client updateClientSecret(String clientId, String secret) {
        var client = findByClientId(clientId);
        client.setClientSecret(encoder.encode(secret));
        return clientRepository.save(client);
    }

    @Override
    @CacheEvict(value = "registered-clients", key = "#root.args[0]")
    public Client updateClient(String clientId, Client newClient) {
        var client = findByClientId(clientId);

        assignNonEmptyFields(newClient, client);

        return clientRepository.save(newClient);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        var client = findByClientId(clientId);

        clientRepository.deleteById(client.getId());
    }

    @Override
    public List<Client> listClientDetails() {
        return clientRepository.findAll();
    }

    private Client createDefaultClient() {
        var authority = new Authority();
        authority.setName("CLIENT");
        authority.setDescription("user can view stuff");

        var defaultClient = new Client();
        defaultClient.setId(generateUUID());
        defaultClient.setAccessTokenValiditySeconds(10 * 60);
        defaultClient.setRefreshTokenValiditySeconds(15 * 60);
        defaultClient.setScope(List.of("read", "write"));
        defaultClient.setAuthorizedGrantTypes(List.of(PASSWORD));
        defaultClient.setAuthorities(List.of(authority));

        return defaultClient;
    }

    private Authority createClientAuthority() {
        return new Authority(AuthorityEnum.CLIENT);
    }

    private void assignNonEmptyFields(Client from, Client to) {
        applyIfNonNull(from.getId(), to::setId);
        applyIfNonNull(from.getName(), to::setName);
        applyIfNonNull(from.getClientId(), to::setClientId);
        applyIfNonNull(from.getClientSecret(), to::setClientSecret);
        applyIfNonNull(from.getScope(), to::setScope);
        applyIfNonNull(from.getResourceIds(), to::setResourceIds);
        applyIfNonNull(from.getAuthorizedGrantTypes(), to::setAuthorizedGrantTypes);
        applyIfNonNull(from.getRegisteredRedirectUri(), to::setRegisteredRedirectUri);
        applyIfNonNull(from.getAutoApproveScopes(), to::setAutoApproveScopes);
        applyIfNonNull(from.getAuthorities(), to::setAuthorities);
        applyIfNonNull(from.getAccessTokenValiditySeconds(), to::setAccessTokenValiditySeconds);
        applyIfNonNull(from.getRefreshTokenValiditySeconds(), to::setRefreshTokenValiditySeconds);
        applyIfNonNull(from.getAdditionalInformation(), to::setAdditionalInformation);
    }
}
