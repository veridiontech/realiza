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
                .companyName(providerSupplierRequestDto.getCompanyName())
                .tradeName(providerSupplierRequestDto.getTradeName())
                .fantasyName(providerSupplierRequestDto.getFantasyName())
                .email(providerSupplierRequestDto.getEmail())
                .cep(providerSupplierRequestDto.getCep())
                .state(providerSupplierRequestDto.getState())
                .city(providerSupplierRequestDto.getCity())
                .address(providerSupplierRequestDto.getAddress())
                .number(providerSupplierRequestDto.getNumber())
                .client(client)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .companyName(savedProviderSupplier.getCompanyName())
                .tradeName(savedProviderSupplier.getTradeName())
                .fantasyName(savedProviderSupplier.getFantasyName())
                .email(savedProviderSupplier.getEmail())
                .cep(savedProviderSupplier.getCep())
                .state(savedProviderSupplier.getState())
                .city(savedProviderSupplier.getCity())
                .address(savedProviderSupplier.getAddress())
                .number(savedProviderSupplier.getNumber())
                .client(savedProviderSupplier.getClient().getIdClient())
                .build();

        return providerSupplierResponse;
    }

    @Override
    public Optional<ProviderResponseDto> findOne(String id) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(id);

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider not found"));

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .idProvider(providerSupplier.getIdProvider())
                .cnpj(providerSupplier.getCnpj())
                .companyName(providerSupplier.getCompanyName())
                .tradeName(providerSupplier.getTradeName())
                .fantasyName(providerSupplier.getFantasyName())
                .email(providerSupplier.getEmail())
                .cep(providerSupplier.getCep())
                .state(providerSupplier.getState())
                .city(providerSupplier.getCity())
                .address(providerSupplier.getAddress())
                .number(providerSupplier.getNumber())
                .client(providerSupplier.getClient().getIdClient())
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public Page<ProviderResponseDto> findAll(Pageable pageable) {
        Page<ProviderSupplier> providerSupplierPage = providerSupplierRepository.findAll(pageable);

        Page<ProviderResponseDto> providerSupplierResponseDtoPage = providerSupplierPage.map(
                providerSupplier -> ProviderResponseDto.builder()
                        .idProvider(providerSupplier.getIdProvider())
                        .cnpj(providerSupplier.getCnpj())
                        .companyName(providerSupplier.getCompanyName())
                        .tradeName(providerSupplier.getTradeName())
                        .fantasyName(providerSupplier.getFantasyName())
                        .email(providerSupplier.getEmail())
                        .cep(providerSupplier.getCep())
                        .state(providerSupplier.getState())
                        .city(providerSupplier.getCity())
                        .address(providerSupplier.getAddress())
                        .number(providerSupplier.getNumber())
                        .client(providerSupplier.getClient().getIdClient())
                        .build()
        );

        return providerSupplierResponseDtoPage;
    }

    @Override
    public Optional<ProviderResponseDto> update(ProviderSupplierRequestDto providerSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(providerSupplierRequestDto.getIdProvider());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Provider not found"));

        providerSupplier.setCnpj(providerSupplierRequestDto.getCnpj() != null ? providerSupplierRequestDto.getCnpj() : providerSupplier.getCnpj());
        providerSupplier.setCompanyName(providerSupplierRequestDto.getCompanyName() != null ? providerSupplierRequestDto.getCompanyName() : providerSupplier.getCompanyName());
        providerSupplier.setTradeName(providerSupplierRequestDto.getTradeName() != null ? providerSupplierRequestDto.getTradeName() : providerSupplier.getTradeName());
        providerSupplier.setFantasyName(providerSupplierRequestDto.getFantasyName() != null ? providerSupplierRequestDto.getFantasyName() : providerSupplier.getFantasyName());
        providerSupplier.setEmail(providerSupplierRequestDto.getEmail() != null ? providerSupplierRequestDto.getEmail() : providerSupplier.getEmail());
        providerSupplier.setCep(providerSupplierRequestDto.getCep() != null ? providerSupplierRequestDto.getCep() : providerSupplier.getCep());
        providerSupplier.setState(providerSupplierRequestDto.getState() != null ? providerSupplierRequestDto.getState() : providerSupplier.getState());
        providerSupplier.setCity(providerSupplierRequestDto.getCity() != null ? providerSupplierRequestDto.getCity() : providerSupplier.getCity());
        providerSupplier.setAddress(providerSupplierRequestDto.getAddress() != null ? providerSupplierRequestDto.getAddress() : providerSupplier.getAddress());
        providerSupplier.setNumber(providerSupplierRequestDto.getNumber() != null ? providerSupplierRequestDto.getNumber() : providerSupplier.getNumber());
        providerSupplier.setIsActive(providerSupplierRequestDto.getIsActive() != null ? providerSupplierRequestDto.getIsActive() : providerSupplier.getIsActive());

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(providerSupplier);

        ProviderResponseDto providerSupplierResponse = ProviderResponseDto.builder()
                .idProvider(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .companyName(savedProviderSupplier.getCompanyName())
                .tradeName(savedProviderSupplier.getTradeName())
                .fantasyName(savedProviderSupplier.getFantasyName())
                .email(savedProviderSupplier.getEmail())
                .cep(savedProviderSupplier.getCep())
                .state(savedProviderSupplier.getState())
                .city(savedProviderSupplier.getCity())
                .address(savedProviderSupplier.getAddress())
                .number(savedProviderSupplier.getNumber())
                .client(savedProviderSupplier.getClient().getIdClient())
                .build();

        return Optional.of(providerSupplierResponse);
    }

    @Override
    public void delete(String id) {
        providerSupplierRepository.deleteById(id);
    }

    @Override
    public Page<ProviderResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<ProviderSupplier> providerSupplierPage = providerSupplierRepository.findAllByClient_IdClient(idSearch, pageable);

        Page<ProviderResponseDto> providerSupplierResponseDtoPage = providerSupplierPage.map(
                providerSupplier -> ProviderResponseDto.builder()
                        .idProvider(providerSupplier.getIdProvider())
                        .cnpj(providerSupplier.getCnpj())
                        .companyName(providerSupplier.getCompanyName())
                        .tradeName(providerSupplier.getTradeName())
                        .fantasyName(providerSupplier.getFantasyName())
                        .email(providerSupplier.getEmail())
                        .cep(providerSupplier.getCep())
                        .state(providerSupplier.getState())
                        .city(providerSupplier.getCity())
                        .address(providerSupplier.getAddress())
                        .number(providerSupplier.getNumber())
                        .client(providerSupplier.getClient().getIdClient())
                        .build()
        );

        return providerSupplierResponseDtoPage;
    }
}
