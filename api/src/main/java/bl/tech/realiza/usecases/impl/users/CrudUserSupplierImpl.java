package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.users.UserProvider;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserSupplierRepository;
import bl.tech.realiza.gateways.requests.users.UserSupplierRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import bl.tech.realiza.gateways.responses.users.UserSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserSupplierImpl implements CrudUserSupplier {

    private final UserSupplierRepository userSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;

    @Override
    public UserSupplierResponseDto save(UserSupplierRequestDto userSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(userSupplierRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        UserProvider newUserSupplier = UserProvider.builder()
                .cpf(userSupplierRequestDto.getCpf())
                .description(userSupplierRequestDto.getDescription())
                .password(userSupplierRequestDto.getPassword())
                .position(userSupplierRequestDto.getPosition())
                .role(userSupplierRequestDto.getRole())
                .firstName(userSupplierRequestDto.getFirstName())
                .timeZone(userSupplierRequestDto.getTimeZone())
                .surname(userSupplierRequestDto.getSurname())
                .email(userSupplierRequestDto.getEmail())
                .profilePicture(userSupplierRequestDto.getProfilePicture())
                .telephone(userSupplierRequestDto.getTelephone())
                .cellphone(userSupplierRequestDto.getCellphone())
                .providerSupplier(providerSupplier)
                .build();

        UserProvider savedUserSupplier = userSupplierRepository.save(newUserSupplier);

        UserSupplierResponseDto userSupplierResponse = UserSupplierResponseDto.builder()
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
    public Optional<UserSupplierResponseDto> findOne(String id) {

        Optional<UserProvider> userProviderOptional = userSupplierRepository.findById(id);

        UserProvider userProvider = userProviderOptional.orElseThrow(() -> new RuntimeException("User not found"));

        UserSupplierResponseDto userSupplierResponse = UserSupplierResponseDto.builder()
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
    public Page<UserSupplierResponseDto> findAll(Pageable pageable) {

        Page<UserProvider> userProviderPage = userSupplierRepository.findAll(pageable);

        Page<UserSupplierResponseDto> userSupplierResponseDtoPage = userProviderPage.map(
                userProvider -> UserSupplierResponseDto.builder()
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
    public Optional<UserSupplierResponseDto> update(UserSupplierRequestDto userSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        providerSupplierRepository.deleteById(id);
    }
}
