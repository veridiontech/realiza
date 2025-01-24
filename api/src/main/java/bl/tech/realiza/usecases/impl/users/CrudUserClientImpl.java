package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.requests.users.UserClientRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.users.CrudUserClient;
import jakarta.persistence.EntityNotFoundException;
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
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public UserResponseDto save(UserClientRequestDto userClientRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(userClientRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new EntityNotFoundException("Client not found"));

        String encryptedPassword = passwordEncryptionService.encryptPassword(userClientRequestDto.getPassword());

        UserClient newUserClient = UserClient.builder()
                .cpf(userClientRequestDto.getCpf())
                .description(userClientRequestDto.getDescription())
                .password(encryptedPassword)
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

        UserResponseDto userClientResponse = UserResponseDto.builder()
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .description(savedUserClient.getDescription())
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
    public Optional<UserResponseDto> findOne(String id) {
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserResponseDto userClientResponse = UserResponseDto.builder()
                .idUser(userClient.getIdUser())
                .cpf(userClient.getCpf())
                .description(userClient.getDescription())
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
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAll(pageable);

        Page<UserResponseDto> userClientResponseDtoPage = userClientPage.map(
                userClient -> UserResponseDto.builder()
                        .idUser(userClient.getIdUser())
                        .cpf(userClient.getCpf())
                        .description(userClient.getDescription())
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
    public Optional<UserResponseDto> update(String id, UserClientRequestDto userClientRequestDto) {
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        userClient.setCpf(userClientRequestDto.getCpf() != null ? userClientRequestDto.getCpf() : userClient.getCpf());
        userClient.setDescription(userClientRequestDto.getDescription() != null ? userClientRequestDto.getDescription() : userClient.getDescription());
        userClient.setPosition(userClientRequestDto.getPosition() != null ? userClientRequestDto.getPosition() : userClient.getPosition());
        userClient.setRole(userClientRequestDto.getRole() != null ? userClientRequestDto.getRole() : userClient.getRole());
        userClient.setFirstName(userClientRequestDto.getFirstName() != null ? userClientRequestDto.getFirstName() : userClient.getFirstName());
        userClient.setTimeZone(userClientRequestDto.getTimeZone() != null ? userClientRequestDto.getTimeZone() : userClient.getTimeZone());
        userClient.setSurname(userClientRequestDto.getSurname() != null ? userClientRequestDto.getSurname() : userClient.getSurname());
        userClient.setEmail(userClientRequestDto.getEmail() != null ? userClientRequestDto.getEmail() : userClient.getEmail());
        userClient.setProfilePicture(userClientRequestDto.getProfilePicture() != null ? userClientRequestDto.getProfilePicture() : userClient.getProfilePicture());
        userClient.setTelephone(userClientRequestDto.getTelephone() != null ? userClientRequestDto.getTelephone() : userClient.getTelephone());
        userClient.setCellphone(userClientRequestDto.getCellphone() != null ? userClientRequestDto.getCellphone() : userClient.getCellphone());
        userClient.setIsActive(userClientRequestDto.getIsActive() != null ? userClientRequestDto.getIsActive() : userClient.getIsActive());

        UserClient savedUserClient = userClientRepository.save(userClient);

        UserResponseDto userClientResponse = UserResponseDto.builder()
                .idUser(savedUserClient.getIdUser())
                .cpf(savedUserClient.getCpf())
                .description(savedUserClient.getDescription())
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

        return Optional.of(userClientResponse);
    }

    @Override
    public void delete(String id) {
        userClientRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<UserClient> userClientPage = userClientRepository.findAllByClient_IdClient(idSearch, pageable);

        Page<UserResponseDto> userClientResponseDtoPage = userClientPage.map(
                userClient -> UserResponseDto.builder()
                        .idUser(userClient.getIdUser())
                        .cpf(userClient.getCpf())
                        .description(userClient.getDescription())
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
    public String changePassword(String id, UserClientRequestDto userClientRequestDto) {
        Optional<UserClient> userClientOptional = userClientRepository.findById(id);

        UserClient userClient = userClientOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userClientRequestDto.getPassword(), userClient.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userClient.setPassword(userClientRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userClientRequestDto.getPassword()) : userClient.getPassword());

        userClientRepository.save(userClient);

        return "Password updated successfully";
    }
}
