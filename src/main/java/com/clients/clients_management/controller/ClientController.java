package com.clients.clients_management.controller;

import com.clients.clients_management.dto.ClientDto;
import com.clients.clients_management.model.Client;
import com.clients.clients_management.service.ClientNotFoundException;
import com.clients.clients_management.service.ClientService;
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

    @GetMapping
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ClientDto getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id).orElseThrow(() -> new ClientNotFoundException(id));
        return toDto(client);
    }

    @PostMapping
    public ClientDto createClient(@RequestBody ClientDto clientDto) {
        Client client = toEntity(clientDto);
        return toDto(clientService.createClient(client));
    }

    @PutMapping("/{id}")
    public ClientDto updateClient(@PathVariable Long id, @RequestBody ClientDto clientDto) {
        Client updatedClient = clientService.updateClient(id, toEntity(clientDto));
        return toDto(updatedClient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    private ClientDto toDto(Client client) {
        return new ClientDto(client.getId(), client.getFirstName(), client.getLastName(), client.getEmail());
    }

    private Client toEntity(ClientDto dto) {
        Client client = new Client();
        client.setId(dto.id());
        client.setFirstName(dto.firstName());
        client.setLastName(dto.lastName());
        client.setEmail(dto.email());
        return client;
    }
}
