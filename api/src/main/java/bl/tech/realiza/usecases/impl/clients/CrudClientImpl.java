package bl.tech.realiza.usecases.impl.clients;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.enterprises.EnterpriseAndUserRequestDto;
import bl.tech.realiza.gateways.requests.clients.ClientRequestDto;
import bl.tech.realiza.gateways.responses.enterprises.EnterpriseAndUserResponseDto;
import bl.tech.realiza.gateways.responses.clients.ClientResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.clients.CrudClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class CrudClientImpl implements CrudClient {

    private final ClientRepository clientRepository;
    private final UserClientRepository userClientRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public ClientResponseDto save(ClientRequestDto clientRequestDto) {
        Client newClient = Client.builder()
                .cnpj(clientRequestDto.getCnpj())
                .tradeName(clientRequestDto.getTradeName())
                .companyName(clientRequestDto.getCompanyName())
                .fantasyName(clientRequestDto.getFantasyName())
                .email(clientRequestDto.getEmail())
                .telephone(clientRequestDto.getTelephone())
                .cep(clientRequestDto.getCep())
                .state(clientRequestDto.getState())
                .city(clientRequestDto.getCity())
                .address(clientRequestDto.getAddress())
                .number(clientRequestDto.getNumber())
                .build();

        Client savedClient = clientRepository.save(newClient);

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .companyName(savedClient.getCompanyName())
                .fantasyName(savedClient.getFantasyName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .cep(savedClient.getCep())
                .state(savedClient.getState())
                .city(savedClient.getCity())
                .address(savedClient.getAddress())
                .number(savedClient.getNumber())
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
                .fantasyName(client.getFantasyName())
                .email(client.getEmail())
                .telephone(client.getTelephone())
                .cep(client.getCep())
                .state(client.getState())
                .city(client.getCity())
                .address(client.getAddress())
                .number(client.getNumber())
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
                        .fantasyName(client.getFantasyName())
                        .email(client.getEmail())
                        .telephone(client.getTelephone())
                        .cep(client.getCep())
                        .state(client.getState())
                        .city(client.getCity())
                        .address(client.getAddress())
                        .number(client.getNumber())
                        .build()
        );

        return clientResponseDtoPage;
    }

    @Override
    public Optional<ClientResponseDto> update(ClientRequestDto clientRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(clientRequestDto.getIdClient());

        Client client = clientOptional.orElseThrow(() -> new EntityNotFoundException("Client not found"));

        client.setCnpj(clientRequestDto.getCnpj() != null ? clientRequestDto.getCnpj() : client.getCnpj());
        client.setTradeName(clientRequestDto.getTradeName() != null ? clientRequestDto.getTradeName() : client.getTradeName());
        client.setCompanyName(clientRequestDto.getCompanyName() != null ? clientRequestDto.getCompanyName() : client.getCompanyName());
        client.setEmail(clientRequestDto.getEmail() != null ? clientRequestDto.getEmail() : client.getEmail());
        client.setTelephone(clientRequestDto.getTelephone() != null ? clientRequestDto.getTelephone() : client.getTelephone());
        client.setCep(clientRequestDto.getCep() != null ? clientRequestDto.getCep() : client.getCep());
        client.setState(client.getState() != null ? clientRequestDto.getState() : client.getState());
        client.setCity(client.getCity() != null ? clientRequestDto.getCity() : client.getCity());
        client.setAddress(client.getAddress() != null ? clientRequestDto.getAddress() : client.getAddress());
        client.setNumber(client.getNumber() != null ? clientRequestDto.getNumber() : client.getNumber());
        client.setIsActive(clientRequestDto.getIsActive() != null ? clientRequestDto.getIsActive() : client.getIsActive());

        Client savedClient = clientRepository.save(client);

        ClientResponseDto clientResponse = ClientResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .companyName(savedClient.getCompanyName())
                .fantasyName(savedClient.getFantasyName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .cep(savedClient.getCep())
                .state(savedClient.getState())
                .city(savedClient.getCity())
                .address(savedClient.getAddress())
                .number(savedClient.getNumber())
                .build();

        return Optional.of(clientResponse);
    }

    @Override
    public void delete(String id) {
        clientRepository.deleteById(id);
    }

    @Override
    public EnterpriseAndUserResponseDto saveBoth(EnterpriseAndUserRequestDto enterpriseAndUserRequestDto) {
        Client newClient = Client.builder()
                .cnpj(enterpriseAndUserRequestDto.getCnpj())
                .tradeName(enterpriseAndUserRequestDto.getNameEnterprise())
                .companyName(enterpriseAndUserRequestDto.getSocialReason())
                .email(enterpriseAndUserRequestDto.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
                .build();

        Client savedClient = clientRepository.save(newClient);

        String encryptedPassword = passwordEncryptionService.encryptPassword(enterpriseAndUserRequestDto.getPassword());

        UserClient newUserClient = UserClient.builder()
                .cpf(enterpriseAndUserRequestDto.getCpf())
                .password(encryptedPassword)
                .position(enterpriseAndUserRequestDto.getPosition())
                .role(enterpriseAndUserRequestDto.getRole())
                .firstName(enterpriseAndUserRequestDto.getName())
                .surname(enterpriseAndUserRequestDto.getSurname())
                .email(enterpriseAndUserRequestDto.getEmail())
                .telephone(enterpriseAndUserRequestDto.getPhone())
                .client(savedClient)
                .build();

        UserClient savedUserClient = userClientRepository.save(newUserClient);

        EnterpriseAndUserResponseDto enterpriseAndUserResponseDto = EnterpriseAndUserResponseDto.builder()
                .idClient(savedClient.getIdClient())
                .cnpj(savedClient.getCnpj())
                .tradeName(savedClient.getTradeName())
                .companyName(savedClient.getCompanyName())
                .email(savedClient.getEmail())
                .telephone(savedClient.getTelephone())
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .firstName(savedUserClient.getFirstName())
                .surname(savedUserClient.getSurname())
                .build();

        return enterpriseAndUserResponseDto;
    }
}
