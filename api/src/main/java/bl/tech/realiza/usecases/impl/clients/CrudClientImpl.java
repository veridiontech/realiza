package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.usecases.interfaces.clients.CrudClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudClientImpl implements CrudClient {

    private final ClientRepository clientRepository;

    @Override
    public ClientResponseDto save(ClientRequestDto clientRequestDto) {

        Client newClient = Client.builder()
                .cnpj(clientRequestDto.getCnpj())
                .tradeName(clientRequestDto.getTradeName())
                .companyName(clientRequestDto.getCompanyName())
                .email(clientRequestDto.getEmail())
                .telephone(clientRequestDto.getTelephone())
                .staff(clientRequestDto.getStaff())
                .customers(clientRequestDto.getCustomers())
                .build();

        Client savedClient = clientRepository.save(newClient);

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .companyName(savedClient.getCompanyName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .staff(savedClient.getStaff())
                .customers(savedClient.getCustomers())
                .build();

        return clientResponse;
    }

    @Override
    public Optional<ClientResponseDto> findOne(String id) {

        Optional<Client> clientOptional = clientRepository.findById(id);

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .cnpj(client.getCnpj())
                .tradeName(client.getTradeName())
                .companyName(client.getCompanyName())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .staff(client.getStaff())
                .customers(client.getCustomers())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public Page<ClientResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ClientResponseDto> update(ClientRequestDto clientRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
