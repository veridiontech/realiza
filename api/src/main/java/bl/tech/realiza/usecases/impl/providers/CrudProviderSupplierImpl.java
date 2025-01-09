package bl.tech.realiza.usecases.impl.providers;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.providers.ProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
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
    public ProviderResponseDto save(ProviderSupplierRequestDto providerSupplierRequestDto) {
        Optional<Client> clientOptional = clientRepository.findById(providerSupplierRequestDto.getClient());

        Client client = clientOptional.orElseThrow(() -> new RuntimeException("Client not found"));

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(providerSupplierRequestDto.getCnpj())
                .client(client)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .id_provider(savedProviderSupplier.getId_provider())
                .cnpj(savedProviderSupplier.getCnpj())
                .client(savedProviderSupplier.getClient().getIdClient())
                .build();

        return providerSupplierResponse;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(id);

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider not found"));

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .id_provider(providerSupplier.getId_provider())
                .cnpj(providerSupplier.getCnpj())
                .client(providerSupplier.getClient().getIdClient())
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        Page<ProviderSupplier> providerSupplierPage = providerSupplierRepository.findAll(pageable);

        Page<ProviderResponseDto> providerSupplierResponseDtoPage = providerSupplierPage.map(
                providerSupplier -> ProviderResponseDto.builder()
                        .id_provider(providerSupplier.getId_provider())
                        .cnpj(providerSupplier.getCnpj())
                        .client(providerSupplier.getClient().getIdClient())
                        .build()
        );

        return providerSupplierResponseDtoPage;
    }

    @Override
    public Optional<ProviderResponseDto> update(ProviderSupplierRequestDto providerSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(providerSupplierRequestDto.getId_provider());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider not found"));

        providerSupplier.setCnpj(providerSupplierRequestDto.getCnpj() != null ? providerSupplierRequestDto.getCnpj() : providerSupplier.getCnpj());

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(providerSupplier);

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .id_provider(savedProviderSupplier.getId_provider())
                .cnpj(savedProviderSupplier.getCnpj())
                .client(savedProviderSupplier.getClient().getIdClient())
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public void delete(String id) {
        providerSupplierRepository.deleteById(id);
    }
}
