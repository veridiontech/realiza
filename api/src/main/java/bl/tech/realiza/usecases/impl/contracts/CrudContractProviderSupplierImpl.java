package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractProviderSupplierRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractProviderResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudContractProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrudContractProviderSupplierImpl implements CrudContractProviderSupplier {

    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ActivityRepository activityRepository;
    private final RequirementRepository requirementRepository;

    @Override
    public ContractProviderResponseDto save(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(contractProviderSupplierRequestDto.getProviderSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<Activity> activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getActivities());
        if (activities.isEmpty()) {
            throw new RuntimeException("Activities not found");
        }

        List<Requirement> requirements = requirementRepository.findAllById(contractProviderSupplierRequestDto.getRequirements());
        if (requirements.isEmpty()) {
            throw new RuntimeException("Requirements not found");
        }

        ContractProviderSupplier newContractSupplier = ContractProviderSupplier.builder()
                .serviceType(contractProviderSupplierRequestDto.getServiceType())
                .serviceDuration(contractProviderSupplierRequestDto.getServiceDuration())
                .serviceName(contractProviderSupplierRequestDto.getServiceName())
                .description(contractProviderSupplierRequestDto.getDescription())
                .allocatedLimit(contractProviderSupplierRequestDto.getAllocatedLimit())
                .startDate(contractProviderSupplierRequestDto.getStartDate())
                .endDate(contractProviderSupplierRequestDto.getEndDate())
                .activities(activities)
                .requirements(requirements)
                .providerSupplier(providerSupplier)
                .build();

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(newContractSupplier);

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .startDate(savedContractProviderSupplier.getStartDate())
                .endDate(savedContractProviderSupplier.getEndDate())
                .activities(savedContractProviderSupplier.getActivities())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getFantasyName())
                .build();

        return contractProviderResponseDto;
    }

    @Override
    public Optional<ContractProviderResponseDto> findOne(String id) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(id);

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .idContract(contractProviderSupplier.getIdContract())
                .serviceType(contractProviderSupplier.getServiceType())
                .serviceDuration(contractProviderSupplier.getServiceDuration())
                .serviceName(contractProviderSupplier.getServiceName())
                .description(contractProviderSupplier.getDescription())
                .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                .startDate(contractProviderSupplier.getStartDate())
                .endDate(contractProviderSupplier.getEndDate())
                .activities(contractProviderSupplier.getActivities())
                .requirements(contractProviderSupplier.getRequirements())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getFantasyName())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public Page<ContractProviderResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAll(pageable);

        Page<ContractProviderResponseDto> providerResponseDtoPage = contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractProviderResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceType())
                        .serviceDuration(contractProviderSupplier.getServiceDuration())
                        .serviceName(contractProviderSupplier.getServiceName())
                        .description(contractProviderSupplier.getDescription())
                        .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                        .startDate(contractProviderSupplier.getStartDate())
                        .endDate(contractProviderSupplier.getEndDate())
                        .activities(contractProviderSupplier.getActivities())
                        .requirements(contractProviderSupplier.getRequirements())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier().getFantasyName())
                        .build()
        );

        return providerResponseDtoPage;
    }

    @Override
    public Optional<ContractProviderResponseDto> update(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(contractProviderSupplierRequestDto.getIdContract());

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();

        if (contractProviderSupplierRequestDto.getActivities() != null) {
            activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getActivities());
            if (activities.isEmpty()) {
                throw new RuntimeException("Activities not found");
            }
        }

        if (contractProviderSupplierRequestDto.getRequirements() != null) {
            requirements = requirementRepository.findAllById(contractProviderSupplierRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new RuntimeException("Requirements not found");
            }
        }

        contractProviderSupplier.setServiceType(contractProviderSupplierRequestDto.getServiceType() != null ? contractProviderSupplierRequestDto.getServiceType() : contractProviderSupplier.getServiceType());
        contractProviderSupplier.setServiceDuration(contractProviderSupplierRequestDto.getServiceDuration() != null ? contractProviderSupplierRequestDto.getServiceDuration() : contractProviderSupplier.getServiceDuration());
        contractProviderSupplier.setServiceName(contractProviderSupplierRequestDto.getServiceName() != null ? contractProviderSupplierRequestDto.getServiceName() : contractProviderSupplier.getServiceName());
        contractProviderSupplier.setDescription(contractProviderSupplierRequestDto.getDescription() != null ? contractProviderSupplierRequestDto.getDescription() : contractProviderSupplier.getDescription());
        contractProviderSupplier.setAllocatedLimit(contractProviderSupplierRequestDto.getAllocatedLimit() != null ? contractProviderSupplierRequestDto.getAllocatedLimit() : contractProviderSupplier.getAllocatedLimit());
        contractProviderSupplier.setStartDate(contractProviderSupplierRequestDto.getStartDate() != null ? contractProviderSupplierRequestDto.getStartDate() : contractProviderSupplier.getStartDate());
        contractProviderSupplier.setEndDate(contractProviderSupplierRequestDto.getEndDate() != null ? contractProviderSupplierRequestDto.getEndDate() : contractProviderSupplier.getEndDate());
        contractProviderSupplier.setActivities(contractProviderSupplierRequestDto.getActivities() != null ? activities : contractProviderSupplier.getActivities());
        contractProviderSupplier.setRequirements(contractProviderSupplierRequestDto.getRequirements() != null ? requirements : contractProviderSupplier.getRequirements());
        contractProviderSupplier.setIsActive(contractProviderSupplierRequestDto.getIsActive() != null ? contractProviderSupplierRequestDto.getIsActive() : contractProviderSupplier.getIsActive());

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(contractProviderSupplier);

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .startDate(savedContractProviderSupplier.getStartDate())
                .endDate(savedContractProviderSupplier.getEndDate())
                .activities(savedContractProviderSupplier.getActivities())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getFantasyName())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public void delete(String id) {
        contractProviderSupplierRepository.deleteById(id);
    }
}
