package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contracts.ContractSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.contracts.ContractSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractSubcontractorResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudSubcontractor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
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

        Optional<ContractSubcontractor> contractSubcontractorOptional = contractSubcontractorRepository.findById(id);

        ContractSubcontractor contractSubcontractor = contractSubcontractorOptional.orElseThrow(() -> new RuntimeException("Contract not found"));

        ContractSubcontractorResponseDto contractSubcontractorResponse = ContractSubcontractorResponseDto.builder()
                .id_contract(contractSubcontractor.getId_contract())
                .service_type(contractSubcontractor.getService_type())
                .service_duration(contractSubcontractor.getService_duration())
                .service_name(contractSubcontractor.getService_name())
                .description(contractSubcontractor.getDescription())
                .allocated_limit(contractSubcontractor.getAllocated_limit())
                .start_date(contractSubcontractor.getStart_date())
                .end_date(contractSubcontractor.getEnd_date())
                // activity
                // requirements
                .contract_reference(contractSubcontractor.getContract_reference())
                .providerSubcontractor(contractSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return Optional.of(contractSubcontractorResponse);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {

        Page<ContractSubcontractor> contractSubcontractorPage = contractSubcontractorRepository.findAll(pageable);

        Page<ContractSubcontractorResponseDto> contractSubcontractorResponsePage = contractSubcontractorPage.map(
                contractSubcontractor -> ContractSubcontractorResponseDto.builder()
                        .id_contract(contractSubcontractor.getId_contract())
                        .service_type(contractSubcontractor.getService_type())
                        .service_duration(contractSubcontractor.getService_duration())
                        .service_name(contractSubcontractor.getService_name())
                        .description(contractSubcontractor.getDescription())
                        .allocated_limit(contractSubcontractor.getAllocated_limit())
                        .start_date(contractSubcontractor.getStart_date())
                        .end_date(contractSubcontractor.getEnd_date())
                        // activity
                        // requirements
                        .contract_reference(contractSubcontractor.getContract_reference())
                        .providerSubcontractor(contractSubcontractor.getProviderSubcontractor().getId_provider())
                        .build()
        );

        return contractSubcontractorResponsePage;
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> update(ContractSubcontractorRequestDto contractSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        contractSubcontractorRepository.deleteById(id);
    }
}
