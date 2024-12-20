package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contracts.ContractSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.contracts.ContractSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContractSubcontractorImpl implements CrudSubcontractor {

    private final ContractSubcontractorRepository contractSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;

    @Override
    public ContractSubcontractorResponseDto save(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {

        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(contractSubcontractorRequestDto.getProviderSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        ContractSubcontractor newContractSubcontractor = ContractSubcontractor.builder()
                .service_type(contractSubcontractorRequestDto.getService_type())
                .service_duration(contractSubcontractorRequestDto.getService_duration())
                .service_name(contractSubcontractorRequestDto.getService_name())
                .description(contractSubcontractorRequestDto.getDescription())
                .allocated_limit(contractSubcontractorRequestDto.getAllocated_limit())
                .start_date(contractSubcontractorRequestDto.getStart_date())
                .end_date(contractSubcontractorRequestDto.getEnd_date())
                // activity
                // requirements
                .contract_reference(contractSubcontractorRequestDto.getContract_reference())
                .providerSubcontractor(providerSubcontractor)
                .build();

        ContractSubcontractor savedContractSubcontractor = contractSubcontractorRepository.save(newContractSubcontractor);

        ContractSubcontractorResponseDto contractSubcontractorResponse = ContractSubcontractorResponseDto.builder()
                .id_contract(savedContractSubcontractor.getId_contract())
                .service_type(savedContractSubcontractor.getService_type())
                .service_duration(savedContractSubcontractor.getService_duration())
                .service_name(savedContractSubcontractor.getService_name())
                .description(savedContractSubcontractor.getDescription())
                .allocated_limit(savedContractSubcontractor.getAllocated_limit())
                .start_date(savedContractSubcontractor.getStart_date())
                .end_date(savedContractSubcontractor.getEnd_date())
                // activity
                // requirements
                .contract_reference(savedContractSubcontractor.getContract_reference())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return contractSubcontractorResponse;
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> findOne(String id) {
        return Optional.empty();
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> update(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {

    }
}
