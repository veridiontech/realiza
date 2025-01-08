package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSubcontractorRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudContractProviderSubcontractor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContractProviderSubcontractorImpl implements CrudContractProviderSubcontractor {

    private final ContractProviderSubcontractorRepository contractProviderSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ActivityRepository activityRepository;
    private final RequirementRepository requirementRepository;

    @Override
    public ContractProviderResponseDto save(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto) {
        Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(contractProviderSubcontractorRequestDto.getProviderSubcontractor());

        ProviderSubcontractor providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new RuntimeException("Subcontractor not found"));

        List<Activity> activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getActivities());
        if (activities.isEmpty()) {
            throw new RuntimeException("Activities not found");
        }

        List<Requirement> requirements = requirementRepository.findAllById(contractProviderSubcontractorRequestDto.getRequirements());
        if (requirements.isEmpty()) {
            throw new RuntimeException("Requirements not found");
        }

        ContractProviderSubcontractor newContractSubcontractor = ContractProviderSubcontractor.builder()
                .service_type(contractProviderSubcontractorRequestDto.getService_type())
                .service_duration(contractProviderSubcontractorRequestDto.getService_duration())
                .service_name(contractProviderSubcontractorRequestDto.getService_name())
                .description(contractProviderSubcontractorRequestDto.getDescription())
                .allocated_limit(contractProviderSubcontractorRequestDto.getAllocated_limit())
                .start_date(contractProviderSubcontractorRequestDto.getStart_date())
                .end_date(contractProviderSubcontractorRequestDto.getEnd_date())
                .activities(activities)
                .requirements(requirements)
                .contract_reference(contractProviderSubcontractorRequestDto.getContract_reference())
                .providerSubcontractor(providerSubcontractor)
                .build();

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(newContractSubcontractor);

        ContractProviderResponseDto contractSubcontractorResponse = ContractProviderResponseDto.builder()
                .id_contract(savedContractSubcontractor.getId_contract())
                .service_type(savedContractSubcontractor.getService_type())
                .service_duration(savedContractSubcontractor.getService_duration())
                .service_name(savedContractSubcontractor.getService_name())
                .description(savedContractSubcontractor.getDescription())
                .allocated_limit(savedContractSubcontractor.getAllocated_limit())
                .start_date(savedContractSubcontractor.getStart_date())
                .end_date(savedContractSubcontractor.getEnd_date())
                .activities(savedContractSubcontractor.getActivities())
                .requirements(savedContractSubcontractor.getRequirements())
                .contract_reference(savedContractSubcontractor.getContract_reference())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return contractSubcontractorResponse;
    }

    @Override
    public Optional<ContractProviderResponseDto> findOne(String id) {

        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new RuntimeException("ContractProvider not found"));

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .id_contract(contractProviderSubcontractor.getId_contract())
                .service_type(contractProviderSubcontractor.getService_type())
                .service_duration(contractProviderSubcontractor.getService_duration())
                .service_name(contractProviderSubcontractor.getService_name())
                .description(contractProviderSubcontractor.getDescription())
                .allocated_limit(contractProviderSubcontractor.getAllocated_limit())
                .start_date(contractProviderSubcontractor.getStart_date())
                .end_date(contractProviderSubcontractor.getEnd_date())
                .activities(contractProviderSubcontractor.getActivities())
                .requirements(contractProviderSubcontractor.getRequirements())
                .contract_reference(contractProviderSubcontractor.getContract_reference())
                .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getId_provider())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public Page<ContractProviderResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAll(pageable);

        Page<ContractProviderResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractProviderResponseDto.builder()
                        .id_contract(contractProviderSubcontractor.getId_contract())
                        .service_type(contractProviderSubcontractor.getService_type())
                        .service_duration(contractProviderSubcontractor.getService_duration())
                        .service_name(contractProviderSubcontractor.getService_name())
                        .description(contractProviderSubcontractor.getDescription())
                        .allocated_limit(contractProviderSubcontractor.getAllocated_limit())
                        .start_date(contractProviderSubcontractor.getStart_date())
                        .end_date(contractProviderSubcontractor.getEnd_date())
                        .activities(contractProviderSubcontractor.getActivities())
                        .requirements(contractProviderSubcontractor.getRequirements())
                        .contract_reference(contractProviderSubcontractor.getContract_reference())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getId_provider())
                        .build()
        );

        return contractProviderResponseDtoPage;
    }

    @Override
    public Optional<ContractProviderResponseDto> update(ContractProviderSubcontractorRequestDto contractProviderSubcontractorRequestDto) {
        return Optional.empty();
    }

    @Override
    public void delete(String id) {
        contractProviderSubcontractorRepository.deleteById(id);
    }
}
