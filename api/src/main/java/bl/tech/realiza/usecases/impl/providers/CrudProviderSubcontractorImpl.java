package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.services.email.EmailSender;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudProviderSubcontractorImpl implements CrudProviderSubcontractor {

    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final EmailSender emailSender;

    @Override
    public ProviderResponseDto save(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(providerSubcontractorRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider supplier not found"));

        ProviderSubcontractor newProviderSubcontractor = ProviderSubcontractor.builder()
                .cnpj(providerSubcontractorRequestDto.getCnpj())
                .companyName(providerSubcontractorRequestDto.getCompanyName())
                .tradeName(providerSubcontractorRequestDto.getTradeName())
                .fantasyName(providerSubcontractorRequestDto.getFantasyName())
                .email(providerSubcontractorRequestDto.getEmail())
                .cep(providerSubcontractorRequestDto.getCep())
                .state(providerSubcontractorRequestDto.getState())
                .city(providerSubcontractorRequestDto.getCity())
                .address(providerSubcontractorRequestDto.getAddress())
                .number(providerSubcontractorRequestDto.getNumber())
                .providerSupplier(providerSupplier)
                .build();

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(newProviderSubcontractor);

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSubcontractor.getIdProvider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .companyName(savedProviderSubcontractor.getCompanyName())
                .tradeName(savedProviderSubcontractor.getTradeName())
                .fantasyName(savedProviderSubcontractor.getFantasyName())
                .email(savedProviderSubcontractor.getEmail())
                .cep(savedProviderSubcontractor.getCep())
                .state(savedProviderSubcontractor.getState())
                .city(savedProviderSubcontractor.getCity())
                .address(savedProviderSubcontractor.getAddress())
                .number(savedProviderSubcontractor.getNumber())
                .supplier(savedProviderSubcontractor.getProviderSupplier().getIdProvider())
                .build();

        return providerSubcontractorResponse;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(id);

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Provider subcontractor not found"));

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .idProvider(providerSubcontractor.getIdProvider())
                .cnpj(providerSubcontractor.getCnpj())
                .companyName(providerSubcontractor.getCompanyName())
                .tradeName(providerSubcontractor.getTradeName())
                .fantasyName(providerSubcontractor.getFantasyName())
                .email(providerSubcontractor.getEmail())
                .cep(providerSubcontractor.getCep())
                .state(providerSubcontractor.getState())
                .city(providerSubcontractor.getCity())
                .address(providerSubcontractor.getAddress())
                .number(providerSubcontractor.getNumber())
                .supplier(providerSubcontractor.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(providerSubcontractorResponse);
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        Page<ProviderSubcontractor> providerSubcontractorPage = providerSubcontractorRepository.findAll(pageable);

        Page<ProviderResponseDto> providerSubcontractorResponseDtoPage = providerSubcontractorPage.map(
                providerSubcontractor -> ProviderResponseDto.builder()
                        .idProvider(providerSubcontractor.getIdProvider())
                        .cnpj(providerSubcontractor.getCnpj())
                        .companyName(providerSubcontractor.getCompanyName())
                        .tradeName(providerSubcontractor.getTradeName())
                        .fantasyName(providerSubcontractor.getFantasyName())
                        .email(providerSubcontractor.getEmail())
                        .cep(providerSubcontractor.getCep())
                        .state(providerSubcontractor.getState())
                        .city(providerSubcontractor.getCity())
                        .address(providerSubcontractor.getAddress())
                        .number(providerSubcontractor.getNumber())
                        .supplier(providerSubcontractor.getProviderSupplier().getIdProvider())
                        .build()
        );

        return providerSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<ProviderResponseDto> update(String id, ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(id);

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Provider subcontractor not found"));

        providerSubcontractor.setCnpj(providerSubcontractorRequestDto.getCnpj() != null ? providerSubcontractorRequestDto.getCnpj() : providerSubcontractor.getCnpj());
        providerSubcontractor.setCompanyName(providerSubcontractorRequestDto.getCompanyName());
        providerSubcontractor.setTradeName(providerSubcontractorRequestDto.getTradeName());
        providerSubcontractor.setFantasyName(providerSubcontractorRequestDto.getFantasyName());
        providerSubcontractor.setEmail(providerSubcontractorRequestDto.getEmail());
        providerSubcontractor.setCep(providerSubcontractorRequestDto.getCep());
        providerSubcontractor.setState(providerSubcontractorRequestDto.getState());
        providerSubcontractor.setCity(providerSubcontractorRequestDto.getCity());
        providerSubcontractor.setAddress(providerSubcontractorRequestDto.getAddress());
        providerSubcontractor.setNumber(providerSubcontractorRequestDto.getNumber());

        providerSubcontractor.setIsActive(providerSubcontractorRequestDto.getIsActive() != null ? providerSubcontractorRequestDto.getIsActive() : providerSubcontractor.getIsActive());

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(providerSubcontractor);

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSubcontractor.getIdProvider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .companyName(savedProviderSubcontractor.getCompanyName())
                .tradeName(savedProviderSubcontractor.getTradeName())
                .fantasyName(savedProviderSubcontractor.getFantasyName())
                .email(savedProviderSubcontractor.getEmail())
                .cep(savedProviderSubcontractor.getCep())
                .state(savedProviderSubcontractor.getState())
                .city(savedProviderSubcontractor.getCity())
                .address(savedProviderSubcontractor.getAddress())
                .number(savedProviderSubcontractor.getNumber())
                .supplier(savedProviderSubcontractor.getProviderSupplier().getIdProvider())
                .build();

        return Optional.of(providerSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        providerSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ProviderResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ProviderSubcontractor> providerSubcontractorPage = providerSubcontractorRepository.findAllByProviderSupplier_IdProvider(idSearch, pageable);

        Page<ProviderResponseDto> providerSubcontractorResponseDtoPage = providerSubcontractorPage.map(
                providerSubcontractor -> ProviderResponseDto.builder()
                        .idProvider(providerSubcontractor.getIdProvider())
                        .cnpj(providerSubcontractor.getCnpj())
                        .companyName(providerSubcontractor.getCompanyName())
                        .tradeName(providerSubcontractor.getTradeName())
                        .fantasyName(providerSubcontractor.getFantasyName())
                        .email(providerSubcontractor.getEmail())
                        .cep(providerSubcontractor.getCep())
                        .state(providerSubcontractor.getState())
                        .city(providerSubcontractor.getCity())
                        .address(providerSubcontractor.getAddress())
                        .number(providerSubcontractor.getNumber())
                        .supplier(providerSubcontractor.getProviderSupplier().getIdProvider())
                        .build()
        );

        return providerSubcontractorResponseDtoPage;
    }
}
