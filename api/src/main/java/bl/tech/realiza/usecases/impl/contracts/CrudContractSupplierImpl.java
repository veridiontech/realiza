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

        return contractSupplierResponse;
    }

    @Override
    public Optional<ContractSupplierResponseDto> findOne(String id) {

        Optional<ContractSupplier> contractSupplierOptional = contractSupplierRepository.findById(id);

        ContractSupplier contractSupplier = contractSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        ContractSupplierResponseDto contractSupplierResponse = ContractSupplierResponseDto.builder()
                .id_contract(contractSupplier.getId_contract())
                .service_type(contractSupplier.getService_type())
                .service_duration(contractSupplier.getService_duration())
                .service_name(contractSupplier.getService_name())
                .description(contractSupplier.getDescription())
                .allocated_limit(contractSupplier.getAllocated_limit())
                .start_date(contractSupplier.getStart_date())
                .end_date(contractSupplier.getEnd_date())
                // activity
                // requirements
                .providerSupplier(contractSupplier.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(contractSupplierResponse);
    }

    @Override
    public Page<ContractSupplierResponseDto> findAll(Pageable pageable) {

        Page<ContractSupplier> contractSupplierPage = contractSupplierRepository.findAll(pageable);

        Page<ContractSupplierResponseDto> contractSupplierResponseDtoPage = contractSupplierPage.map(
                contractSupplier -> ContractSupplierResponseDto.builder()
                        .id_contract(contractSupplier.getId_contract())
                        .service_type(contractSupplier.getService_type())
                        .service_duration(contractSupplier.getService_duration())
                        .service_name(contractSupplier.getService_name())
                        .description(contractSupplier.getDescription())
                        .allocated_limit(contractSupplier.getAllocated_limit())
                        .start_date(contractSupplier.getStart_date())
                        .end_date(contractSupplier.getEnd_date())
                        // activity
                        // requirements
                        .providerSupplier(contractSupplier.getProviderSupplier().getId_provider())
                        .build()
        );

        return contractSupplierResponseDtoPage;
    }

    @Override
    public Optional<ContractSupplierResponseDto> update(ContractSupplierRequestDto contractSupplierRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        contractSupplierRepository.deleteById(id);
    }
}
