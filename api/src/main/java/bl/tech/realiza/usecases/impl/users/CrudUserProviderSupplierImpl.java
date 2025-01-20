package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.PasswordEncryptionService;
import bl.tech.realiza.usecases.interfaces.users.CrudUserProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserProviderSupplierImpl implements CrudUserProviderSupplier {

    private final UserProviderSupplierRepository userSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final PasswordEncryptionService passwordEncryptionService;

    @Override
    public UserResponseDto save(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(userProviderSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        String encryptedPassword = passwordEncryptionService.encryptPassword(userProviderSupplierRequestDto.getPassword());

        UserProviderSupplier newUserSupplier = UserProviderSupplier.builder()
                .cpf(userProviderSupplierRequestDto.getCpf())
                .description(userProviderSupplierRequestDto.getDescription())
                .password(encryptedPassword)
                .position(userProviderSupplierRequestDto.getPosition())
                .role(userProviderSupplierRequestDto.getRole())
                .firstName(userProviderSupplierRequestDto.getFirstName())
                .timeZone(userProviderSupplierRequestDto.getTimeZone())
                .surname(userProviderSupplierRequestDto.getSurname())
                .email(userProviderSupplierRequestDto.getEmail())
                .profilePicture(userProviderSupplierRequestDto.getProfilePicture())
                .telephone(userProviderSupplierRequestDto.getTelephone())
                .cellphone(userProviderSupplierRequestDto.getCellphone())
                .providerSupplier(providerSupplier)
                .build();

        UserProviderSupplier savedUserSupplier = userSupplierRepository.save(newUserSupplier);

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(savedUserSupplier.getCpf())
                .description(savedUserSupplier.getDescription())
                .password(savedUserSupplier.getPassword())
                .position(savedUserSupplier.getPosition())
                .role(savedUserSupplier.getRole())
                .firstName(savedUserSupplier.getFirstName())
                .timeZone(savedUserSupplier.getTimeZone())
                .surname(savedUserSupplier.getSurname())
                .email(savedUserSupplier.getEmail())
                .profilePicture(savedUserSupplier.getProfilePicture())
                .telephone(savedUserSupplier.getTelephone())
                .cellphone(savedUserSupplier.getCellphone())
                .supplier(savedUserSupplier.getProviderSupplier().getIdProvider())
                .build();

        return userSupplierResponse;
    }

    @Override
    public Optional<UserResponseDto> findOne(String id) {
        Optional<UserProviderSupplier> userProviderOptional = userSupplierRepository.findById(id);

        UserProviderSupplier userProvider = userProviderOptional.orElseThrow(() -> new RuntimeException("User not found"));

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(userProvider.getCpf())
                .description(userProvider.getDescription())
                .password(userProvider.getPassword())
                .position(userProvider.getPosition())
                .role(userProvider.getRole())
                .firstName(userProvider.getFirstName())
                .timeZone(userProvider.getTimeZone())
                .surname(userProvider.getSurname())
                .email(userProvider.getEmail())
                .profilePicture(userProvider.getProfilePicture())
                .telephone(userProvider.getTelephone())
                .cellphone(userProvider.getCellphone())
                .supplier(userProvider.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(userSupplierResponse);
    }

    @Override
    public Page<UserResponseDto> findAll(Pageable pageable) {
        Page<UserProviderSupplier> userProviderPage = userSupplierRepository.findAll(pageable);

        Page<UserResponseDto> userSupplierResponseDtoPage = userProviderPage.map(
                userProvider -> UserResponseDto.builder()
                        .cpf(userProvider.getCpf())
                        .description(userProvider.getDescription())
                        .password(userProvider.getPassword())
                        .position(userProvider.getPosition())
                        .role(userProvider.getRole())
                        .firstName(userProvider.getFirstName())
                        .timeZone(userProvider.getTimeZone())
                        .surname(userProvider.getSurname())
                        .email(userProvider.getEmail())
                        .profilePicture(userProvider.getProfilePicture())
                        .telephone(userProvider.getTelephone())
                        .cellphone(userProvider.getCellphone())
                        .supplier(userProvider.getProviderSupplier().getIdProvider())
                        .build()
        );

        return userSupplierResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        Optional<UserProviderSupplier> userProviderOptional = userSupplierRepository.findById(userProviderSupplierRequestDto.getIdUser());

        UserProviderSupplier userProvider = userProviderOptional.orElseThrow(() -> new RuntimeException("User not found"));

        userProvider.setCpf(userProviderSupplierRequestDto.getCpf() != null ? userProviderSupplierRequestDto.getCpf() : userProvider.getCpf());
        userProvider.setDescription(userProviderSupplierRequestDto.getDescription() != null ? userProviderSupplierRequestDto.getDescription() : userProvider.getDescription());
        userProvider.setPassword(passwordEncryptionService.encryptPassword(userProviderSupplierRequestDto.getPassword()) != null ? passwordEncryptionService.encryptPassword(userProviderSupplierRequestDto.getPassword()) : userProvider.getPassword());
        userProvider.setPosition(userProviderSupplierRequestDto.getPosition() != null ? userProviderSupplierRequestDto.getPosition() : userProvider.getPosition());
        userProvider.setRole(userProviderSupplierRequestDto.getRole() != null ? userProviderSupplierRequestDto.getRole() : userProvider.getRole());
        userProvider.setFirstName(userProviderSupplierRequestDto.getFirstName() != null ? userProviderSupplierRequestDto.getFirstName() : userProvider.getFirstName());
        userProvider.setTimeZone(userProviderSupplierRequestDto.getTimeZone() != null ? userProviderSupplierRequestDto.getTimeZone() : userProvider.getTimeZone());
        userProvider.setSurname(userProviderSupplierRequestDto.getSurname() != null ? userProviderSupplierRequestDto.getSurname() : userProvider.getSurname());
        userProvider.setEmail(userProviderSupplierRequestDto.getEmail() != null ? userProviderSupplierRequestDto.getEmail() : userProvider.getEmail());
        userProvider.setProfilePicture(userProviderSupplierRequestDto.getProfilePicture() != null ? userProviderSupplierRequestDto.getProfilePicture() : userProvider.getProfilePicture());
        userProvider.setTelephone(userProviderSupplierRequestDto.getTelephone() != null ? userProviderSupplierRequestDto.getTelephone() : userProvider.getTelephone());
        userProvider.setCellphone(userProviderSupplierRequestDto.getCellphone() != null ? userProviderSupplierRequestDto.getCellphone() : userProvider.getCellphone());
        userProvider.setIsActive(userProviderSupplierRequestDto.getIsActive() != null ? userProviderSupplierRequestDto.getIsActive() : userProvider.getIsActive());

        UserProviderSupplier savedUserSupplier = userSupplierRepository.save(userProvider);

        UserResponseDto userSupplierResponse = UserResponseDto.builder()
                .cpf(savedUserSupplier.getCpf())
                .description(savedUserSupplier.getDescription())
                .password(savedUserSupplier.getPassword())
                .position(savedUserSupplier.getPosition())
                .role(savedUserSupplier.getRole())
                .firstName(savedUserSupplier.getFirstName())
                .timeZone(savedUserSupplier.getTimeZone())
                .surname(savedUserSupplier.getSurname())
                .email(savedUserSupplier.getEmail())
                .profilePicture(savedUserSupplier.getProfilePicture())
                .telephone(savedUserSupplier.getTelephone())
                .cellphone(savedUserSupplier.getCellphone())
                .supplier(savedUserSupplier.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(userSupplierResponse);
    }

    @Override
    public void delete(String id) {
        userSupplierRepository.deleteById(id);
    }

    @Override
    public Page<UserResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<UserProviderSupplier> userProviderPage = userSupplierRepository.findAllByProviderSupplier_IdProvider(idSearch, pageable);

        Page<UserResponseDto> userSupplierResponseDtoPage = userProviderPage.map(
                userProvider -> UserResponseDto.builder()
                        .cpf(userProvider.getCpf())
                        .description(userProvider.getDescription())
                        .password(userProvider.getPassword())
                        .position(userProvider.getPosition())
                        .role(userProvider.getRole())
                        .firstName(userProvider.getFirstName())
                        .timeZone(userProvider.getTimeZone())
                        .surname(userProvider.getSurname())
                        .email(userProvider.getEmail())
                        .profilePicture(userProvider.getProfilePicture())
                        .telephone(userProvider.getTelephone())
                        .cellphone(userProvider.getCellphone())
                        .supplier(userProvider.getProviderSupplier().getIdProvider())
                        .build()
        );

        return userSupplierResponseDtoPage;
    }
}
