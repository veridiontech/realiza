package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudProviderSupplierImpl implements CrudProviderSupplier {

    private final ProviderSupplierRepository providerSupplierRepository;
    private final ClientRepository clientRepository;

    @Override
    public ProviderSupplierResponseDto save(ProviderSupplierRequestDto providerSupplierRequestDto) {

        Optional<Client> clientOptional = clientRepository.findById(providerSupplierRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(providerSupplierRequestDto.getCnpj())
                .client(client)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        ProviderSupplierResponseDto providerSupplierResponse = ProviderSupplierResponseDto.builder()
                .id_provider(savedProviderSupplier.getId_provider())
                .cnpj(savedProviderSupplier.getCnpj())
                .client(savedProviderSupplier.getClient().getIdClient())
                .build();

        return providerSupplierResponse;
    }

    @Override
    public Optional<ProviderSupplierResponseDto> findOne(String id) {

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(id);

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider not found"));

        ProviderSupplierResponseDto providerSupplierResponse = ProviderSupplierResponseDto.builder()
                .id_provider(providerSupplier.getId_provider())
                .cnpj(providerSupplier.getCnpj())
                .client(providerSupplier.getClient().getIdClient())
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public Page<ProviderSupplierResponseDto> findAll(Pageable pageable) {

        Page<ProviderSupplier> providerSupplierPage = providerSupplierRepository.findAll(pageable);

        Page<ProviderSupplierResponseDto> providerSupplierResponseDtoPage = providerSupplierPage.map(
                providerSupplier -> ProviderSupplierResponseDto.builder()
                        .id_provider(providerSupplier.getId_provider())
                        .cnpj(providerSupplier.getCnpj())
                        .client(providerSupplier.getClient().getIdClient())
                        .build()
        );

        return providerSupplierResponseDtoPage;
    }

    @Override
    public Optional<ProviderSupplierResponseDto> update(ProviderSupplierRequestDto providerSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        providerSupplierRepository.deleteById(id);
    }
}
