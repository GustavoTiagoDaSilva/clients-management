package com.clients.clients_management.service;

import com.clients.clients_management.model.Client;
import com.clients.clients_management.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final RestClient restClient;
    private final String uiAvatarsApiUrl;

    public ClientService(ClientRepository clientRepository,
                         RestClient restClient,
                         @Value("${api.ui-avatars.url}") String uiAvatarsApiUrl) {
        this.clientRepository = clientRepository;
        this.restClient = restClient;
        this.uiAvatarsApiUrl = uiAvatarsApiUrl;
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(Long id) {
        Optional<Client> client = clientRepository.findById(id);
        if (client.isEmpty()) {
            throw new ClientNotFoundException(id);
        }
        return client;
    }

    public Client createClient(Client client) {
        String firstName = client.getFirstName();
        String lastName = client.getLastName();
        String uiAvatar = restClient
                .get()
                .uri(this.uiAvatarsApiUrl.concat("?name=").concat(firstName).concat("+").concat(lastName))
                .retrieve()
                .body(String.class);
        // Remove null bytes from SVG string
        if (uiAvatar != null) {
            uiAvatar = uiAvatar.replace("\u0000", "");
        }
        client.setUiAvatar(uiAvatar);
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails) {
        Client client = clientRepository.findById(id)
            .orElseThrow(() -> new ClientNotFoundException(id));
        client.setFirstName(clientDetails.getFirstName());
        client.setLastName(clientDetails.getLastName());
        client.setEmail(clientDetails.getEmail());
        return clientRepository.save(client);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
