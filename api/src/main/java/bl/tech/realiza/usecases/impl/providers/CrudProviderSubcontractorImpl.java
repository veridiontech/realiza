package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.providers.ProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudProviderSubcontractorImpl implements CrudProviderSubcontractor {

    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ProviderSupplierRepository providerSupplierRepository;

    @Override
    public ProviderResponseDto save(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(providerSubcontractorRequestDto.getSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider supplier not found"));

        ProviderSubcontractor newProviderSubcontractor = ProviderSubcontractor.builder()
                .cnpj(providerSubcontractorRequestDto.getCnpj())
                .providerSupplier(providerSupplier)
                .build();

        ProviderSubcontractor savedProviderSubcontractor = providerSubcontractorRepository.save(newProviderSubcontractor);

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .id_provider(savedProviderSubcontractor.getId_provider())
                .cnpj(savedProviderSubcontractor.getCnpj())
                .supplier(savedProviderSubcontractor.getProviderSupplier().getId_provider())
                .build();

        return providerSubcontractorResponse;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(id);

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Provider subcontractor not found"));

        ProviderResponseDto providerSubcontractorResponse = ProviderResponseDto.builder()
                .id_provider(providerSubcontractor.getId_provider())
                .cnpj(providerSubcontractor.getCnpj())
                .supplier(providerSubcontractor.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(providerSubcontractorResponse);
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        Page<ProviderSubcontractor> providerSubcontractorPage = providerSubcontractorRepository.findAll(pageable);

        Page<ProviderResponseDto> providerSubcontractorResponseDtoPage = providerSubcontractorPage.map(
                providerSubcontractor -> ProviderResponseDto.builder()
                        .id_provider(providerSubcontractor.getId_provider())
                        .cnpj(providerSubcontractor.getCnpj())
                        .supplier(providerSubcontractor.getProviderSupplier().getId_provider())
                        .build()
        );

        return providerSubcontractorResponseDtoPage;
    }

    @Override
    public Optional<ProviderResponseDto> update(ProviderSubcontractorRequestDto providerSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        providerSubcontractorRepository.deleteById(id);
    }
}
