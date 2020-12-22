package dev.rexijie.auth.controller.registration.client;

import dev.rexijie.auth.controller.registration.dto.ClientDto;
import dev.rexijie.auth.controller.registration.dto.mapper.ClientMapper;
import dev.rexijie.auth.service.ClientService;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// TODO - Update Client Registration
@RestController
@RequestMapping("/api/clients")
public class ClientRegistrationEndpoint {

    private final ClientService clientService;

    public ClientRegistrationEndpoint(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<ClientDto> addClient(@RequestBody ClientRegistrationRequest clientRegistrationRequest) {
        var client = clientRegistrationRequest.toClient();
        var savedClient = clientService.addClient(client);
        var clientDto = ClientMapper.toDto(savedClient);
        var headers = new HttpHeaders();
        headers.setCacheControl(CacheControl.noCache());
        return new ResponseEntity<>(clientDto, headers, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Map<String, ?>> updateClient() {
        var status = Map.of("status", "not implimented");
        return new ResponseEntity<>(status, HttpStatus.NOT_IMPLEMENTED);
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
