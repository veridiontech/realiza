package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserManager;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSubcontractorRepository;
import bl.tech.realiza.gateways.requests.users.UserManagerRequestDto;
import bl.tech.realiza.gateways.requests.users.UserProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.users.CrudUserProviderSubcontractor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserProviderSubcontractorImpl implements CrudUserProviderSubcontractor {

    private final UserProviderSubcontractorRepository userSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final EmailSender emailSender;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public UserResponseDto save(UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(userProviderSubcontractorRequestDto.getSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("Subcontractor not found"));

        String encryptedPassword = passwordEncryptionService.encryptPassword(userProviderSubcontractorRequestDto.getPassword());

        UserProviderSubcontractor newUserSubcontractor = UserProviderSubcontractor.builder()
                .cpf(userProviderSubcontractorRequestDto.getCpf())
                .description(userProviderSubcontractorRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userProviderSubcontractorRequestDto.getPosition())
                .role(userProviderSubcontractorRequestDto.getRole())
                .firstName(userProviderSubcontractorRequestDto.getFirstName())
                .timeZone(userProviderSubcontractorRequestDto.getTimeZone())
                .surname(userProviderSubcontractorRequestDto.getSurname())
                .email(userProviderSubcontractorRequestDto.getEmail())
                .profilePicture(userProviderSubcontractorRequestDto.getProfilePicture())
                .telephone(userProviderSubcontractorRequestDto.getTelephone())
                .cellphone(userProviderSubcontractorRequestDto.getCellphone())
                .providerSubcontractor(providerSubcontractor)
                .build();

        UserProviderSubcontractor savedUserSubcontractor = userSubcontractorRepository.save(newUserSubcontractor);

        UserResponseDto userSubcontractorResponse = UserResponseDto.builder()
                .cpf(savedUserSubcontractor.getCpf())
                .description(savedUserSubcontractor.getDescription())
                .position(savedUserSubcontractor.getPosition())
                .role(savedUserSubcontractor.getRole())
                .firstName(savedUserSubcontractor.getFirstName())
                .timeZone(savedUserSubcontractor.getTimeZone())
                .surname(savedUserSubcontractor.getSurname())
                .email(savedUserSubcontractor.getEmail())
                .profilePicture(savedUserSubcontractor.getProfilePicture())
                .telephone(savedUserSubcontractor.getTelephone())
                .cellphone(savedUserSubcontractor.getCellphone())
                .subcontractor(savedUserSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return userSubcontractorResponse;
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        Optional<UserProviderSubcontractor> userSubcontractorOptional = userSubcontractorRepository.findById(id);

        UserProviderSubcontractor userSubcontractor = userSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserResponseDto userSubcontractorResponse = UserResponseDto.builder()
                .cpf(userSubcontractor.getCpf())
                .description(userSubcontractor.getDescription())
                .position(userSubcontractor.getPosition())
                .role(userSubcontractor.getRole())
                .firstName(userSubcontractor.getFirstName())
                .timeZone(userSubcontractor.getTimeZone())
                .surname(userSubcontractor.getSurname())
                .email(userSubcontractor.getEmail())
                .profilePicture(userSubcontractor.getProfilePicture())
                .telephone(userSubcontractor.getTelephone())
                .cellphone(userSubcontractor.getCellphone())
                .subcontractor(userSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return Optional.of(userSubcontractorResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserProviderSubcontractor> userSubcontractorPage = userSubcontractorRepository.findAll(pageable);

        Page<UserResponseDto> userSubcontractorResponseDtoPage = userSubcontractorPage.map(
                userSubcontractor -> UserResponseDto.builder()
                        .cpf(userSubcontractor.getCpf())
                        .description(userSubcontractor.getDescription())
                        .position(userSubcontractor.getPosition())
                        .role(userSubcontractor.getRole())
                        .firstName(userSubcontractor.getFirstName())
                        .timeZone(userSubcontractor.getTimeZone())
                        .surname(userSubcontractor.getSurname())
                        .email(userSubcontractor.getEmail())
                        .profilePicture(userSubcontractor.getProfilePicture())
                        .telephone(userSubcontractor.getTelephone())
                        .cellphone(userSubcontractor.getCellphone())
                        .subcontractor(userSubcontractor.getProviderSubcontractor().getIdProvider())
                        .build()
        );

        return userSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(String id, UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        Optional<UserProviderSubcontractor> userSubcontractorOptional = userSubcontractorRepository.findById(id);

        UserProviderSubcontractor userSubcontractor = userSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        userSubcontractor.setCpf(userProviderSubcontractorRequestDto.getCpf() != null ? userProviderSubcontractorRequestDto.getCpf() : userSubcontractor.getCpf());
        userSubcontractor.setDescription(userProviderSubcontractorRequestDto.getDescription() != null ? userProviderSubcontractorRequestDto.getDescription() : userSubcontractor.getDescription());
        userSubcontractor.setPosition(userProviderSubcontractorRequestDto.getPosition() != null ? userProviderSubcontractorRequestDto.getPosition() : userSubcontractor.getPosition());
        userSubcontractor.setRole(userProviderSubcontractorRequestDto.getRole() != null ? userProviderSubcontractorRequestDto.getRole() : userSubcontractor.getRole());
        userSubcontractor.setFirstName(userProviderSubcontractorRequestDto.getFirstName() != null ? userProviderSubcontractorRequestDto.getFirstName() : userSubcontractor.getFirstName());
        userSubcontractor.setTimeZone(userProviderSubcontractorRequestDto.getTimeZone() != null ? userProviderSubcontractorRequestDto.getTimeZone() : userSubcontractor.getTimeZone());
        userSubcontractor.setSurname(userProviderSubcontractorRequestDto.getSurname() != null ? userProviderSubcontractorRequestDto.getSurname() : userSubcontractor.getSurname());
        userSubcontractor.setEmail(userProviderSubcontractorRequestDto.getEmail() != null ? userProviderSubcontractorRequestDto.getEmail() : userSubcontractor.getEmail());
        userSubcontractor.setProfilePicture(userProviderSubcontractorRequestDto.getProfilePicture() != null ? userProviderSubcontractorRequestDto.getProfilePicture() : userSubcontractor.getProfilePicture());
        userSubcontractor.setTelephone(userProviderSubcontractorRequestDto.getTelephone() != null ? userProviderSubcontractorRequestDto.getTelephone() : userSubcontractor.getTelephone());
        userSubcontractor.setCellphone(userProviderSubcontractorRequestDto.getCellphone() != null ? userProviderSubcontractorRequestDto.getCellphone() : userSubcontractor.getCellphone());
        userSubcontractor.setIsActive(userProviderSubcontractorRequestDto.getIsActive() != null ? userProviderSubcontractorRequestDto.getIsActive() : userSubcontractor.getIsActive());

        UserProviderSubcontractor savedUserSubcontractor = userSubcontractorRepository.save(userSubcontractor);

        UserResponseDto userSubcontractorResponse = UserResponseDto.builder()
                .cpf(savedUserSubcontractor.getCpf())
                .description(savedUserSubcontractor.getDescription())
                .position(savedUserSubcontractor.getPosition())
                .role(savedUserSubcontractor.getRole())
                .firstName(savedUserSubcontractor.getFirstName())
                .timeZone(savedUserSubcontractor.getTimeZone())
                .surname(savedUserSubcontractor.getSurname())
                .email(savedUserSubcontractor.getEmail())
                .profilePicture(savedUserSubcontractor.getProfilePicture())
                .telephone(savedUserSubcontractor.getTelephone())
                .cellphone(savedUserSubcontractor.getCellphone())
                .subcontractor(savedUserSubcontractor.getProviderSubcontractor().getIdProvider())
                .build();

        return Optional.of(userSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        userSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<UserProviderSubcontractor> userSubcontractorPage = userSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndRole(idSearch, User.Role.ROLE_SUBCONTRACTOR, pageable);

        Page<UserResponseDto> userSubcontractorResponseDtoPage = userSubcontractorPage.map(
                userSubcontractor -> UserResponseDto.builder()
                        .cpf(userSubcontractor.getCpf())
                        .description(userSubcontractor.getDescription())
                        .position(userSubcontractor.getPosition())
                        .role(userSubcontractor.getRole())
                        .firstName(userSubcontractor.getFirstName())
                        .timeZone(userSubcontractor.getTimeZone())
                        .surname(userSubcontractor.getSurname())
                        .email(userSubcontractor.getEmail())
                        .profilePicture(userSubcontractor.getProfilePicture())
                        .telephone(userSubcontractor.getTelephone())
                        .cellphone(userSubcontractor.getCellphone())
                        .subcontractor(userSubcontractor.getProviderSubcontractor().getIdProvider())
                        .build()
        );

        return userSubcontractorResponseDtoPage;
    }

    @Override
    public String changePassword(String id, UserProviderSubcontractorRequestDto userProviderSubcontractorRequestDto) {
        Optional<UserProviderSubcontractor> userProviderSubcontractorOptional = userSubcontractorRepository.findById(id);

        UserProviderSubcontractor userProviderSubcontractor = userProviderSubcontractorOptional.orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncryptionService.matches(userProviderSubcontractorRequestDto.getPassword(), userProviderSubcontractor.getPassword())) {
            throw new IllegalArgumentException("Invalid data");
        }

        userProviderSubcontractor.setPassword(userProviderSubcontractorRequestDto.getNewPassword() != null ? passwordEncryptionService.encryptPassword(userProviderSubcontractorRequestDto.getPassword()) : userProviderSubcontractor.getPassword());

        userSubcontractorRepository.save(userProviderSubcontractor);

        return "Password updated successfully";
    }
}
