package com.clients.clients_management.controller;

import com.clients.clients_management.dto.ClientDto;
import com.clients.clients_management.model.Client;
import com.clients.clients_management.service.ClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest
public class ClientControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenNoClients_whenGetAllClients_thenReturnEmptyList() throws Exception {
        when(clientService.getAllClients()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void givenValidClientDto_whenCreateClient_thenReturnCreatedClientDto() throws Exception {
        ClientDto clientDto = new ClientDto(null, "John", "Doe", "john.doe@example.com");
        Client client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        when(clientService.createClient(any(Client.class))).thenReturn(client);
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void givenNonExistentClientId_whenGetClientById_thenReturn404() throws Exception {
        Long nonExistentId = 999L;
        when(clientService.getClientById(nonExistentId)).thenThrow(new com.clients.clients_management.service.ClientNotFoundException(nonExistentId));
        mockMvc.perform(get("/api/clients/" + nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Client with id 999 not found"));
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ClientService clientService() {
            return mock(ClientService.class);
        }
    }
}
