package bl.tech.realiza.usecases.impl.users;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.users.UserSubcontractor;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.users.UserSubcontractorRepository;
import bl.tech.realiza.gateways.requests.users.UserSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import bl.tech.realiza.gateways.responses.users.UserSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.users.CrudUserSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudUserSubcontractorImpl implements CrudUserSubcontractor {

    private final UserSubcontractorRepository userSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;

    @Override
    public UserSubcontractorResponseDto save(UserSubcontractorRequestDto userSubcontractorRequestDto) {

        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(userSubcontractorRequestDto.getSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        UserSubcontractor newUserSubcontractor = UserSubcontractor.builder()
                .cpf(userSubcontractorRequestDto.getCpf())
                .description(userSubcontractorRequestDto.getDescription())
                .password(userSubcontractorRequestDto.getPassword())
                .position(userSubcontractorRequestDto.getPosition())
                .role(userSubcontractorRequestDto.getRole())
                .firstName(userSubcontractorRequestDto.getFirstName())
                .timeZone(userSubcontractorRequestDto.getTimeZone())
                .surname(userSubcontractorRequestDto.getSurname())
                .email(userSubcontractorRequestDto.getEmail())
                .profilePicture(userSubcontractorRequestDto.getProfilePicture())
                .telephone(userSubcontractorRequestDto.getTelephone())
                .cellphone(userSubcontractorRequestDto.getCellphone())
                .providerSubcontractor(providerSubcontractor)
                .build();

        UserSubcontractor savedUserSubcontractor = userSubcontractorRepository.save(newUserSubcontractor);

        UserSubcontractorResponseDto userSubcontractorResponse = UserSubcontractorResponseDto.builder()
                .cpf(savedUserSubcontractor.getCpf())
                .description(savedUserSubcontractor.getDescription())
                .password(savedUserSubcontractor.getPassword())
                .position(savedUserSubcontractor.getPosition())
                .role(savedUserSubcontractor.getRole())
                .firstName(savedUserSubcontractor.getFirstName())
                .timeZone(savedUserSubcontractor.getTimeZone())
                .surname(savedUserSubcontractor.getSurname())
                .email(savedUserSubcontractor.getEmail())
                .profilePicture(savedUserSubcontractor.getProfilePicture())
                .telephone(savedUserSubcontractor.getTelephone())
                .cellphone(savedUserSubcontractor.getCellphone())
                .subcontractor(savedUserSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return userSubcontractorResponse;
    }

    @Override
    public Optional<UserSubcontractorResponseDto> findOne(String id) {

        Optional<UserSubcontractor> userSubcontractorOptional = userSubcontractorRepository.findById(id);

        UserSubcontractor userSubcontractor = userSubcontractorOptional.orElseThrow(() -> new RuntimeException("User not found"));

        UserSubcontractorResponseDto userSubcontractorResponse = UserSubcontractorResponseDto.builder()
                .cpf(userSubcontractor.getCpf())
                .description(userSubcontractor.getDescription())
                .password(userSubcontractor.getPassword())
                .position(userSubcontractor.getPosition())
                .role(userSubcontractor.getRole())
                .firstName(userSubcontractor.getFirstName())
                .timeZone(userSubcontractor.getTimeZone())
                .surname(userSubcontractor.getSurname())
                .email(userSubcontractor.getEmail())
                .profilePicture(userSubcontractor.getProfilePicture())
                .telephone(userSubcontractor.getTelephone())
                .cellphone(userSubcontractor.getCellphone())
                .subcontractor(userSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return Optional.of(userSubcontractorResponse);
    }

    @Override
    public Page<UserSubcontractorResponseDto> findAll(Pageable pageable) {

        Page<UserSubcontractor> userSubcontractorPage = userSubcontractorRepository.findAll(pageable);

        Page<UserSubcontractorResponseDto> userSubcontractorResponseDtoPage = userSubcontractorPage.map(
                userSubcontractor -> UserSubcontractorResponseDto.builder()
                        .cpf(userSubcontractor.getCpf())
                        .description(userSubcontractor.getDescription())
                        .password(userSubcontractor.getPassword())
                        .position(userSubcontractor.getPosition())
                        .role(userSubcontractor.getRole())
                        .firstName(userSubcontractor.getFirstName())
                        .timeZone(userSubcontractor.getTimeZone())
                        .surname(userSubcontractor.getSurname())
                        .email(userSubcontractor.getEmail())
                        .profilePicture(userSubcontractor.getProfilePicture())
                        .telephone(userSubcontractor.getTelephone())
                        .cellphone(userSubcontractor.getCellphone())
                        .subcontractor(userSubcontractor.getProviderSubcontractor().getId_provider())
                        .build()
        );

        return userSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<UserSubcontractorResponseDto> update(UserSubcontractorRequestDto userSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        userSubcontractorRepository.deleteById(id);
    }
}
