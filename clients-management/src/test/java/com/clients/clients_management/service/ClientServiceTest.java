package com.clients.clients_management.service;

import com.clients.clients_management.model.Client;
import com.clients.clients_management.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

import java.util.Optional;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ClientServiceTest {
    private ClientService clientService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private RestClient restClient;
    private String externalApiUrl = "http://dummy-url";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientService(clientRepository, restClient, externalApiUrl);
    }

    @Test
    void givenNoClients_whenGetAllClients_thenReturnEmptyList() {
        when(clientRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(clientService.getAllClients()).isEmpty();
    }

    @Test
    void givenValidClient_whenCreateClient_thenReturnCreatedClient() {
        Client client = new Client();
        when(clientRepository.save(client)).thenReturn(client);
        assertThat(clientService.createClient(client)).isEqualTo(client);
    }

    @Test
    void givenClientId_whenGetClientById_thenReturnClient() {
        Client client = new Client();
        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        assertThat(clientService.getClientById(1L)).isPresent();
    }

    @Test
    void givenNonExistentClientId_whenGetClientById_thenThrowClientNotFoundException() {
        Long nonExistentId = 999L;
        when(clientRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> clientService.getClientById(nonExistentId))
            .isInstanceOf(ClientNotFoundException.class)
            .hasMessageContaining("Client with id 999 not found");
    }

    @Test
    void givenClientId_whenDeleteClient_thenRepositoryDeleteCalled() {
        doNothing().when(clientRepository).deleteById(1L);
        clientService.deleteClient(1L);
        verify(clientRepository, times(1)).deleteById(1L);
    }
}
