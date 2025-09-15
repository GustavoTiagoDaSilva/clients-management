package com.clients.clients_management.controller;

import com.clients.clients_management.dto.ClientDto;
import com.clients.clients_management.model.Client;
import com.clients.clients_management.service.ClientNotFoundException;
import com.clients.clients_management.service.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Operations related to clients management")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    @Operation(summary = "Get all clients", description = "Returns a list of all clients")
    public List<ClientDto> getAllClients() {
        return clientService.getAllClients().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID", description = "Returns a client by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client found"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ClientDto getClientById(@Parameter(description = "ID of the client to retrieve") @PathVariable Long id) {
        Client client = clientService.getClientById(id).orElseThrow(() -> new ClientNotFoundException(id));
        return toDto(client);
    }

    @PostMapping
    @Operation(summary = "Create a new client", description = "Creates a new client")
    @ApiResponse(responseCode = "200", description = "Client created successfully")
    public ClientDto createClient(@RequestBody ClientDto clientDto) {
        Client client = toEntity(clientDto);
        return toDto(clientService.createClient(client));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a client", description = "Updates an existing client")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Client updated successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ClientDto updateClient(@Parameter(description = "ID of the client to update") @PathVariable Long id, @RequestBody ClientDto clientDto) {
        Client updatedClient = clientService.updateClient(id, toEntity(clientDto));
        return toDto(updatedClient);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a client", description = "Deletes a client by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Client deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<Void> deleteClient(@Parameter(description = "ID of the client to delete") @PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    private ClientDto toDto(Client client) {
        return new ClientDto(client.getId(), client.getFirstName(), client.getLastName(), client.getEmail(), client.getUiAvatar());
    }

    private Client toEntity(ClientDto dto) {
        Client client = new Client();
        client.setId(dto.id());
        client.setFirstName(dto.firstName());
        client.setLastName(dto.lastName());
        client.setEmail(dto.email());
        client.setUiAvatar(dto.uiAvatar());
        return client;
    }
}
