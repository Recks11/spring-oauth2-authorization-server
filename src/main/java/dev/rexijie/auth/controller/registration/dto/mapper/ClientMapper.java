package dev.rexijie.auth.controller.registration.dto.mapper;

import dev.rexijie.auth.controller.registration.dto.ClientDto;
import dev.rexijie.auth.model.Client;

public class ClientMapper {
    private ClientMapper() {}

    public static ClientDto toDto(Client client) {
        var clientDto = new ClientDto();
        clientDto.setId(client.getId());
        clientDto.setName(client.getName());
        clientDto.setClientId(client.getClientId());
        clientDto.setClientSecret(clientDto.getClientSecret());

        return clientDto;
    }
}
