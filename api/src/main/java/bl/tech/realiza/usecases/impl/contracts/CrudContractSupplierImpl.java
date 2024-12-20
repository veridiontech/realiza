package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contracts.ContractSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.contracts.ContractSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSupplierResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContractSupplierImpl implements CrudSupplier {

    private final ContractSupplierRepository contractSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;

    @Override
    public ContractSupplierResponseDto save(ContractSupplierRequestDto contractSupplierRequestDto) {

        Optional<ProviderSupplier> providerSupplierOptional  = providerSupplierRepository.findById(contractSupplierRequestDto.getProviderSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        ContractSupplier newContractSupplier = ContractSupplier.builder()
                .service_type(contractSupplierRequestDto.getService_type())
                .service_duration(contractSupplierRequestDto.getService_duration())
                .service_name(contractSupplierRequestDto.getService_name())
                .description(contractSupplierRequestDto.getDescription())
                .allocated_limit(contractSupplierRequestDto.getAllocated_limit())
                .start_date(contractSupplierRequestDto.getStart_date())
                .end_date(contractSupplierRequestDto.getEnd_date())
                // activity
                // requirements
                .providerSupplier(providerSupplier)
                .build();

        ContractSupplier savedContractSupplier = contractSupplierRepository.save(newContractSupplier);

        ContractSupplierResponseDto contractSupplierResponse = ContractSupplierResponseDto.builder()
                .id_contract(savedContractSupplier.getId_contract())
                .service_type(savedContractSupplier.getService_type())
                .service_duration(savedContractSupplier.getService_duration())
                .service_name(savedContractSupplier.getService_name())
                .description(savedContractSupplier.getDescription())
                .allocated_limit(savedContractSupplier.getAllocated_limit())
                .start_date(savedContractSupplier.getStart_date())
                .end_date(savedContractSupplier.getEnd_date())
                // activity
                // requirements
                .providerSupplier(savedContractSupplier.getProviderSupplier().getId_provider())
                .build();

        return null;
    }

    @Override
    public Optional<ContractSupplierResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ContractSupplierResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ContractSupplierResponseDto> update(ContractSupplierRequestDto contractSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
