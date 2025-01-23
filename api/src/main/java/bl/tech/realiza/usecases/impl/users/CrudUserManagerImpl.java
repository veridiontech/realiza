package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.gateways.repositories.users.UserManagerRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.users.CrudUserManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserManagerImpl implements CrudUserManager {
    
    private final UserManagerRepository userManagerRepository;
    private final PasswordEncryptionService passwordEncryptionService;
    
    @Override
    public UserResponseDto save(UserManagerRequestDto userManagerRequestDto) {
        String encryptedPassword = passwordEncryptionService.encryptPassword(userManagerRequestDto.getPassword());

        UserManager newUserManager = UserManager.builder()
                .cpf(userManagerRequestDto.getCpf())
                .description(userManagerRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userManagerRequestDto.getPosition())
                .role(userManagerRequestDto.getRole())
                .firstName(userManagerRequestDto.getFirstName())
                .timeZone(userManagerRequestDto.getTimeZone())
                .surname(userManagerRequestDto.getSurname())
                .email(userManagerRequestDto.getEmail())
                .profilePicture(userManagerRequestDto.getProfilePicture())
                .telephone(userManagerRequestDto.getTelephone())
                .cellphone(userManagerRequestDto.getCellphone())
                .build();

        UserManager savedUserManager = userManagerRepository.save(newUserManager);

        UserResponseDto userManagerResponse = UserResponseDto.builder()
                .idUser(savedUserManager.getIdUser())
                .cpf(savedUserManager.getCpf())
                .description(savedUserManager.getDescription())
                .password(savedUserManager.getPassword())
                .position(savedUserManager.getPosition())
                .role(savedUserManager.getRole())
                .firstName(savedUserManager.getFirstName())
                .timeZone(savedUserManager.getTimeZone())
                .surname(savedUserManager.getSurname())
                .email(savedUserManager.getEmail())
                .profilePicture(savedUserManager.getProfilePicture())
                .telephone(savedUserManager.getTelephone())
                .cellphone(savedUserManager.getCellphone())
                .build();

        return userManagerResponse;
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);

        UserManager userManager = userManagerOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserResponseDto userManagerResponse = UserResponseDto.builder()
                .idUser(userManager.getIdUser())
                .cpf(userManager.getCpf())
                .description(userManager.getDescription())
                .password(userManager.getPassword())
                .position(userManager.getPosition())
                .role(userManager.getRole())
                .firstName(userManager.getFirstName())
                .timeZone(userManager.getTimeZone())
                .surname(userManager.getSurname())
                .email(userManager.getEmail())
                .profilePicture(userManager.getProfilePicture())
                .telephone(userManager.getTelephone())
                .cellphone(userManager.getCellphone())
                .build();

        return Optional.of(userManagerResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserManager> userManagerPage = userManagerRepository.findAll(pageable);

        Page<UserResponseDto> userManagerResponseDtoPage = userManagerPage.map(
                userManager -> UserResponseDto.builder()
                        .idUser(userManager.getIdUser())
                        .cpf(userManager.getCpf())
                        .description(userManager.getDescription())
                        .password(userManager.getPassword())
                        .position(userManager.getPosition())
                        .role(userManager.getRole())
                        .firstName(userManager.getFirstName())
                        .timeZone(userManager.getTimeZone())
                        .surname(userManager.getSurname())
                        .email(userManager.getEmail())
                        .profilePicture(userManager.getProfilePicture())
                        .telephone(userManager.getTelephone())
                        .cellphone(userManager.getCellphone())
                        .build()
        );

        return userManagerResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserManagerRequestDto userManagerRequestDto) {
        Optional<UserManager> userManagerOptional = userManagerRepository.findById(id);

        UserManager userManager = userManagerOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userManagerRequestDto.getPassword(), userManager.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userManager.setCpf(userManagerRequestDto.getCpf() != null ? userManagerRequestDto.getCpf() : userManager.getCpf());
        userManager.setDescription(userManagerRequestDto.getDescription() != null ? userManagerRequestDto.getDescription() : userManager.getDescription());
        userManager.setPassword(userManagerRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userManagerRequestDto.getPassword()) : userManager.getPassword());
        userManager.setPosition(userManagerRequestDto.getPosition() != null ? userManagerRequestDto.getPosition() : userManager.getPosition());
        userManager.setRole(userManagerRequestDto.getRole() != null ? userManagerRequestDto.getRole() : userManager.getRole());
        userManager.setFirstName(userManagerRequestDto.getFirstName() != null ? userManagerRequestDto.getFirstName() : userManager.getFirstName());
        userManager.setTimeZone(userManagerRequestDto.getTimeZone() != null ? userManagerRequestDto.getTimeZone() : userManager.getTimeZone());
        userManager.setSurname(userManagerRequestDto.getSurname() != null ? userManagerRequestDto.getSurname() : userManager.getSurname());
        userManager.setEmail(userManagerRequestDto.getEmail() != null ? userManagerRequestDto.getEmail() : userManager.getEmail());
        userManager.setProfilePicture(userManagerRequestDto.getProfilePicture() != null ? userManagerRequestDto.getProfilePicture() : userManager.getProfilePicture());
        userManager.setTelephone(userManagerRequestDto.getTelephone() != null ? userManagerRequestDto.getTelephone() : userManager.getTelephone());
        userManager.setCellphone(userManagerRequestDto.getCellphone() != null ? userManagerRequestDto.getCellphone() : userManager.getCellphone());
        userManager.setIsActive(userManagerRequestDto.getIsActive() != null ? userManagerRequestDto.getIsActive() : userManager.getIsActive());

        UserManager savedUserManager = userManagerRepository.save(userManager);

        UserResponseDto userManagerResponse = UserResponseDto.builder()
                .idUser(savedUserManager.getIdUser())
                .cpf(savedUserManager.getCpf())
                .description(savedUserManager.getDescription())
                .password(savedUserManager.getPassword())
                .position(savedUserManager.getPosition())
                .role(savedUserManager.getRole())
                .firstName(savedUserManager.getFirstName())
                .timeZone(savedUserManager.getTimeZone())
                .surname(savedUserManager.getSurname())
                .email(savedUserManager.getEmail())
                .profilePicture(savedUserManager.getProfilePicture())
                .telephone(savedUserManager.getTelephone())
                .cellphone(savedUserManager.getCellphone())
                .build();

        return Optional.of(userManagerResponse);
    }

    @Override
    public void delete(String id) {
        userManagerRepository.deleteById(id);
    }
}
