package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.users.User;
import bl.tech.realiza.domains.users.UserClient;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserClientResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserClientImpl implements CrudUserClient {

    private final UserClientRepository userClientRepository;
    private final ClientRepository clientRepository;

    @Override
    public UserClientResponseDto save(UserClientRequestDto userClientRequestDto) {

        Optional<Client> clientOptional = clientRepository.findById(userClientRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        UserClient newUserClient = UserClient.builder()
                .cpf(userClientRequestDto.getCpf())
                .description(userClientRequestDto.getDescription())
                .password(userClientRequestDto.getPassword())
                .position(userClientRequestDto.getPosition())
                .role(userClientRequestDto.getRole())
                .firstName(userClientRequestDto.getFirstName())
                .timeZone(userClientRequestDto.getTimeZone())
                .surname(userClientRequestDto.getSurname())
                .email(userClientRequestDto.getEmail())
                .profilePicture(userClientRequestDto.getProfilePicture())
                .telephone(userClientRequestDto.getTelephone())
                .cellphone(userClientRequestDto.getCellphone())
                .client(client)
                .build();

        UserClient savedUserClient = userClientRepository.save(newUserClient);

        UserClientResponseDto userClientResponse = UserClientResponseDto.builder()
                .cpf(savedUserClient.getCpf())
                .description(savedUserClient.getDescription())
                .password(savedUserClient.getPassword())
                .position(savedUserClient.getPosition())
                .role(savedUserClient.getRole())
                .firstName(savedUserClient.getFirstName())
                .timeZone(savedUserClient.getTimeZone())
                .surname(savedUserClient.getSurname())
                .email(savedUserClient.getEmail())
                .profilePicture(savedUserClient.getProfilePicture())
                .telephone(savedUserClient.getTelephone())
                .cellphone(savedUserClient.getCellphone())
                .client(savedUserClient.getClient().getIdClient())
                .build();

        return userClientResponse;
    }

    @Override
    public Optional<UserClientResponseDto> findOne(String id) {

        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new RuntimeException("User not found"));

        UserClientResponseDto userClientResponse = UserClientResponseDto.builder()
                .cpf(userClient.getCpf())
                .description(userClient.getDescription())
                .password(userClient.getPassword())
                .position(userClient.getPosition())
                .role(userClient.getRole())
                .firstName(userClient.getFirstName())
                .timeZone(userClient.getTimeZone())
                .surname(userClient.getSurname())
                .email(userClient.getEmail())
                .profilePicture(userClient.getProfilePicture())
                .telephone(userClient.getTelephone())
                .cellphone(userClient.getCellphone())
                .client(userClient.getClient().getIdClient())
                .build();

        return Optional.of(userClientResponse);
    }

    @Override
    public Page<UserClientResponseDto> findAll(Pageable pageable) {

        Page<UserClient> userClientPage = userClientRepository.findAll(pageable);

        Page<UserClientResponseDto> userClientResponseDtoPage = userClientPage.map(
                userClient -> UserClientResponseDto.builder()
                        .cpf(userClient.getCpf())
                        .description(userClient.getDescription())
                        .password(userClient.getPassword())
                        .position(userClient.getPosition())
                        .role(userClient.getRole())
                        .firstName(userClient.getFirstName())
                        .timeZone(userClient.getTimeZone())
                        .surname(userClient.getSurname())
                        .email(userClient.getEmail())
                        .profilePicture(userClient.getProfilePicture())
                        .telephone(userClient.getTelephone())
                        .cellphone(userClient.getCellphone())
                        .client(userClient.getClient().getIdClient())
                        .build()
        );

        return userClientResponseDtoPage;
    }

    @Override
    public Optional<UserClientResponseDto> update(UserClientRequestDto userClientRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        userClientRepository.deleteById(id);
    }
}
