package com.benoly.auth.controller.registration.dto.mapper;

import com.benoly.auth.controller.registration.dto.ClientDto;
import com.benoly.auth.model.Client;

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
