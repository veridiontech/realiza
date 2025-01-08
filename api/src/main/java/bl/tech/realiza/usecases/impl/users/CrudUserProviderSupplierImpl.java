package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.users.UserProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
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

    @Override
    public UserResponseDto save(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(userProviderSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        UserProviderSupplier newUserSupplier = UserProviderSupplier.builder()
                .cpf(userProviderSupplierRequestDto.getCpf())
                .description(userProviderSupplierRequestDto.getDescription())
                .password(userProviderSupplierRequestDto.getPassword())
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
                .supplier(savedUserSupplier.getProviderSupplier().getId_provider())
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
                .supplier(userProvider.getProviderSupplier().getId_provider())
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
                        .supplier(userProvider.getProviderSupplier().getId_provider())
                        .build()
        );

        return userSupplierResponseDtoPage;
    }

    @Override
    public Optional<UserResponseDto> update(UserProviderSupplierRequestDto userProviderSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        userSupplierRepository.deleteById(id);
    }
}
