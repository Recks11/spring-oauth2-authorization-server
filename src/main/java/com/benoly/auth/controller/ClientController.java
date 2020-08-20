package com.benoly.auth.controller;

import com.benoly.auth.model.Client;
import com.benoly.auth.model.dto.ClientDto;
import com.benoly.auth.model.dto.mapper.ClientMapper;
import com.benoly.auth.service.ClientService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientDto> addClient(@RequestBody Client client) {
        var savedClient = clientService.addClient(client);
        var clientDto = ClientMapper.toDto(savedClient);
        var headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache());
        return new ResponseEntity<>(clientDto, headers, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        var clients = clientService
                .listClientDetails()
                .parallelStream()
                .map(ClientMapper::toDto)
                .collect(Collectors.toList());
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }
}
