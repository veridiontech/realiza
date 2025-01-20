package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
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
    public ContractResponseDto save(ContractRequestDto contractProviderSubcontractorRequestDto) {
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
                .serviceType(contractProviderSubcontractorRequestDto.getServiceType())
                .serviceDuration(contractProviderSubcontractorRequestDto.getServiceDuration())
                .serviceName(contractProviderSubcontractorRequestDto.getServiceName())
                .contractReference(contractProviderSubcontractorRequestDto.getContractReference())
                .description(contractProviderSubcontractorRequestDto.getDescription())
                .allocatedLimit(contractProviderSubcontractorRequestDto.getAllocatedLimit())
                .startDate(contractProviderSubcontractorRequestDto.getStartDate())
                .endDate(contractProviderSubcontractorRequestDto.getEndDate())
                .activities(activities)
                .requirements(requirements)
                .providerSubcontractor(providerSubcontractor)
                .build();

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(newContractSubcontractor);

        ContractResponseDto contractSubcontractorResponse = ContractResponseDto.builder()
                .idContract(savedContractSubcontractor.getIdContract())
                .serviceType(savedContractSubcontractor.getServiceType())
                .serviceDuration(savedContractSubcontractor.getServiceDuration())
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .allocatedLimit(savedContractSubcontractor.getAllocatedLimit())
                .startDate(savedContractSubcontractor.getStartDate())
                .endDate(savedContractSubcontractor.getEndDate())
                .activities(savedContractSubcontractor.getActivities())
                .requirements(savedContractSubcontractor.getRequirements())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getFantasyName())
                .build();

        return contractSubcontractorResponse;
    }

    @Override
    public Optional<ContractResponseDto> findOne(String id) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new RuntimeException("Contract not found"));

        ContractResponseDto contractProviderResponseDto = ContractResponseDto.builder()
                .idContract(contractProviderSubcontractor.getIdContract())
                .serviceType(contractProviderSubcontractor.getServiceType())
                .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                .serviceName(contractProviderSubcontractor.getServiceName())
                .contractReference(contractProviderSubcontractor.getContractReference())
                .description(contractProviderSubcontractor.getDescription())
                .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
                .startDate(contractProviderSubcontractor.getStartDate())
                .endDate(contractProviderSubcontractor.getEndDate())
                .activities(contractProviderSubcontractor.getActivities())
                .requirements(contractProviderSubcontractor.getRequirements())
                .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getFantasyName())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public Page<ContractResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAll(pageable);

        Page<ContractResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractResponseDto.builder()
                        .idContract(contractProviderSubcontractor.getIdContract())
                        .serviceType(contractProviderSubcontractor.getServiceType())
                        .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
                        .startDate(contractProviderSubcontractor.getStartDate())
                        .endDate(contractProviderSubcontractor.getEndDate())
                        .activities(contractProviderSubcontractor.getActivities())
                        .requirements(contractProviderSubcontractor.getRequirements())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getFantasyName())
                        .build()
        );

        return contractProviderResponseDtoPage;
    }

    @Override
    public Optional<ContractResponseDto> update(ContractRequestDto contractProviderSubcontractorRequestDto) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(contractProviderSubcontractorRequestDto.getIdContract());

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new RuntimeException("Contract not found"));

        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();

        if (contractProviderSubcontractorRequestDto.getActivities() != null) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getActivities());
            if (activities.isEmpty()) {
                throw new RuntimeException("Activities not found");
            }
        }

        if (contractProviderSubcontractorRequestDto.getRequirements() != null) {
            requirements = requirementRepository.findAllById(contractProviderSubcontractorRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new RuntimeException("Requirements not found");
            }
        }

        contractProviderSubcontractor.setServiceType(contractProviderSubcontractorRequestDto.getServiceType() != null ? contractProviderSubcontractorRequestDto.getServiceType() : contractProviderSubcontractor.getServiceType());
        contractProviderSubcontractor.setServiceDuration(contractProviderSubcontractorRequestDto.getServiceDuration() != null ? contractProviderSubcontractorRequestDto.getServiceDuration() : contractProviderSubcontractor.getServiceDuration());
        contractProviderSubcontractor.setServiceName(contractProviderSubcontractorRequestDto.getServiceName() != null ? contractProviderSubcontractorRequestDto.getServiceName() : contractProviderSubcontractor.getServiceName());
        contractProviderSubcontractor.setContractReference(contractProviderSubcontractorRequestDto.getContractReference() != null ? contractProviderSubcontractorRequestDto.getContractReference() : contractProviderSubcontractor.getContractReference());
        contractProviderSubcontractor.setDescription(contractProviderSubcontractorRequestDto.getDescription() != null ? contractProviderSubcontractorRequestDto.getDescription() : contractProviderSubcontractor.getDescription());
        contractProviderSubcontractor.setAllocatedLimit(contractProviderSubcontractorRequestDto.getAllocatedLimit() != null ? contractProviderSubcontractorRequestDto.getAllocatedLimit() : contractProviderSubcontractor.getAllocatedLimit());
        contractProviderSubcontractor.setStartDate(contractProviderSubcontractorRequestDto.getStartDate() != null ? contractProviderSubcontractorRequestDto.getStartDate() : contractProviderSubcontractor.getStartDate());
        contractProviderSubcontractor.setEndDate(contractProviderSubcontractorRequestDto.getEndDate() != null ? contractProviderSubcontractorRequestDto.getEndDate() : contractProviderSubcontractor.getEndDate());
        contractProviderSubcontractor.setActivities(contractProviderSubcontractorRequestDto.getActivities() != null ? activities : contractProviderSubcontractor.getActivities());
        contractProviderSubcontractor.setRequirements(contractProviderSubcontractorRequestDto.getRequirements() != null ? requirements : contractProviderSubcontractor.getRequirements());
        contractProviderSubcontractor.setIsActive(contractProviderSubcontractorRequestDto.getIsActive() != null ? contractProviderSubcontractorRequestDto.getIsActive() : contractProviderSubcontractor.getIsActive());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(contractProviderSubcontractor);

        ContractResponseDto contractSubcontractorResponse = ContractResponseDto.builder()
                .idContract(savedContractSubcontractor.getIdContract())
                .serviceType(savedContractSubcontractor.getServiceType())
                .serviceDuration(savedContractSubcontractor.getServiceDuration())
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .allocatedLimit(savedContractSubcontractor.getAllocatedLimit())
                .startDate(savedContractSubcontractor.getStartDate())
                .endDate(savedContractSubcontractor.getEndDate())
                .activities(savedContractSubcontractor.getActivities())
                .requirements(savedContractSubcontractor.getRequirements())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getFantasyName())
                .build();

        return Optional.of(contractSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        contractProviderSubcontractorRepository.deleteById(id);
    }
}