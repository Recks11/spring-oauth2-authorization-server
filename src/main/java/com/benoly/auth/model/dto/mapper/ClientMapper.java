package com.benoly.auth.model.dto.mapper;

import com.benoly.auth.model.Client;
import com.benoly.auth.model.dto.ClientDto;

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
