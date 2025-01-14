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
                .idClient(savedClient.getIdClient())
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
                .idClient(client.getIdClient())
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
        Page<Client> clientPage = clientRepository.findAll(pageable);

        Page<ClientResponseDto> clientResponseDtoPage = clientPage.map(
                client -> ClientResponseDto.builder()
                        .idClient(client.getIdClient())
                        .cnpj(client.getCnpj())
                        .tradeName(client.getTradeName())
                        .companyName(client.getCompanyName())
                        .email(client.getEmail())
                        .telephone(client.getTelephone())
                        .staff(client.getStaff())
                        .customers(client.getCustomers())
                        .build()
        );

        return clientResponseDtoPage;
    }

    @Override
    public Optional<ClientResponseDto> update(ClientRequestDto clientRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(clientRequestDto.getIdClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        client.setCnpj(clientRequestDto.getCnpj() != null ? clientRequestDto.getCnpj() : client.getCnpj());
        client.setTradeName(clientRequestDto.getTradeName() != null ? clientRequestDto.getTradeName() : client.getTradeName());
        client.setCompanyName(clientRequestDto.getCompanyName() != null ? clientRequestDto.getCompanyName() : client.getCompanyName());
        client.setEmail(clientRequestDto.getEmail() != null ? clientRequestDto.getEmail() : client.getEmail());
        client.setTelephone(clientRequestDto.getTelephone() != null ? clientRequestDto.getTelephone() : client.getTelephone());
        client.setStaff(clientRequestDto.getStaff() != null ? clientRequestDto.getStaff() : client.getStaff());
        client.setCustomers(clientRequestDto.getCustomers() != null ? clientRequestDto.getCustomers() : client.getCustomers());
        client.setIsActive(clientRequestDto.getIsActive() != null ? clientRequestDto.getIsActive() : client.getIsActive());

        Client savedClient = clientRepository.save(client);

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .companyName(savedClient.getCompanyName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .staff(savedClient.getStaff())
                .customers(savedClient.getCustomers())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public void delete(String id) {
        clientRepository.deleteById(id);
    }
}
