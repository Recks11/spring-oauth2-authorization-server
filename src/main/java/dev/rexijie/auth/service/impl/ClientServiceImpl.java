package dev.rexijie.auth.service.impl;

import dev.rexijie.auth.constants.GrantTypes;
import dev.rexijie.auth.model.*;
import dev.rexijie.auth.repository.ClientRepository;
import dev.rexijie.auth.service.ClientService;
import dev.rexijie.auth.service.SecretGenerator;
import dev.rexijie.auth.util.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static dev.rexijie.auth.util.TokenUtils.generateUUID;

@Service
@Slf4j
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final PasswordEncoder encoder;
    private final SecretGenerator secretGenerator;

    public ClientServiceImpl(ClientRepository clientRepository,
                             PasswordEncoder encoder,
                             SecretGenerator secretGenerator) {
        this.clientRepository = clientRepository;
        this.encoder = encoder;
        this.secretGenerator = secretGenerator;
    }

    @Override
    public Client addClient(Client client) {
        var defaultClient = createDefaultClient();
        assignNonEmptyFields(client, defaultClient);

        String secret = secretGenerator.generate();
        if (defaultClient.getClientSecret() == null)
            defaultClient.setClientSecret(encoder.encode(secret));

        var returnedClient = clientRepository.save(defaultClient);
        returnedClient.setClientSecret(secret);

        return returnedClient;
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
            found = this.findByClientId(clientId);
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
        var defaultClient = new Client(null, ClientTypes.CONFIDENTIAL, ClientProfiles.WEB);
        defaultClient.setId(generateUUID());
        defaultClient.setClientId(secretGenerator.generate(8));
        defaultClient.setAccessTokenValiditySeconds(10 * 60);
        defaultClient.setRefreshTokenValiditySeconds(15 * 60);
        defaultClient.setScope(List.of("read", "write", "profile", "openid", "email"));
        defaultClient.setAuthorizedGrantTypes(List.of(GrantTypes.PASSWORD, GrantTypes.AUTHORIZATION_CODE, GrantTypes.REFRESH_TOKEN));
        defaultClient.setAuthorities(List.of(createClientAuthority()));
        defaultClient.setCreatedAt(LocalDateTime.now());

        return defaultClient;
    }

    private Authority createClientAuthority() {
        return new Authority(AuthorityEnum.CLIENT);
    }

    private void assignNonEmptyFields(Client from, Client to) {
        ObjectUtils.applyIfNonNull(from.getId(), to::setId);
        ObjectUtils.applyIfNonNull(from.getClientName(), to::setClientName);
        ObjectUtils.applyIfNonNull(from.getClientType(), to::setClientType);
        ObjectUtils.applyIfNonNull(from.getClientProfile(), to::setClientProfile);
        ObjectUtils.applyIfNonNull(from.getClientId(), to::setClientId);
        ObjectUtils.applyIfNonNull(from.getClientSecret(), to::setClientSecret);
        ObjectUtils.applyIfNonNull(from.getScope(), to::setScope);
        ObjectUtils.applyIfNonNull(from.getResourceIds(), to::setResourceIds);
        ObjectUtils.applyIfNonNull(from.getAuthorizedGrantTypes(), to::setAuthorizedGrantTypes);
        ObjectUtils.applyIfNonNull(from.getRegisteredRedirectUri(), to::setRegisteredRedirectUri);
        ObjectUtils.applyIfNonNull(from.getAutoApproveScopes(), to::setAutoApproveScopes);
        ObjectUtils.applyIfNonNull(from.getAuthorities(), to::setAuthorities);
        ObjectUtils.applyIfNonNull(from.getAccessTokenValiditySeconds(), to::setAccessTokenValiditySeconds);
        ObjectUtils.applyIfNonNull(from.getRefreshTokenValiditySeconds(), to::setRefreshTokenValiditySeconds);
        ObjectUtils.applyIfNonNull(from.getAdditionalInformation(), to::setAdditionalInformation);
//        ObjectUtils.applyIfNonNull(from.getJwksuri(), to::setJwksuri);
//        ObjectUtils.applyIfNonNull(from.getJwks(), to::setJwks);
        ObjectUtils.applyIfNonNull(from.getLogoUri(), to::setLogoUri);
        ObjectUtils.applyIfNonNull(from.getClientUri(), to::setClientUri);
        ObjectUtils.applyIfNonNull(from.getPolicyUri(), to::setPolicyUri);
        ObjectUtils.applyIfNonNull(from.getSelectorIdentifierUri(), to::setSelectorIdentifierUri);
        ObjectUtils.applyIfNonNull(from.getSubjectType(), to::setSubjectType);
        ObjectUtils.applyIfNonNull(from.getTokenEndpointAuthMethod(), to::setTokenEndpointAuthMethod);
        ObjectUtils.applyIfNonNull(from.getDefaultMaxAge(), to::setDefaultMaxAge);
        ObjectUtils.applyIfNonNull(from.isRequireAuthTime(), to::setRequireAuthTime);


    }
}
