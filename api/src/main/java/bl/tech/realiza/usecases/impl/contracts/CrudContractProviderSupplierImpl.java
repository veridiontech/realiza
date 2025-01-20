package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.responses.contracts.ContractResponseDto;
import bl.tech.realiza.usecases.interfaces.contracts.CrudContractProviderSupplier;
import jakarta.persistence.EntityNotFoundException;
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
    public ContractResponseDto save(ContractRequestDto contractProviderSupplierRequestDto) {
        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(contractProviderSupplierRequestDto.getProviderSupplier());

        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        List<Activity> activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getActivities());
        if (activities.isEmpty()) {
            throw new EntityNotFoundException("Activities not found");
        }

        List<Requirement> requirements = requirementRepository.findAllById(contractProviderSupplierRequestDto.getRequirements());
        if (requirements.isEmpty()) {
            throw new EntityNotFoundException("Requirements not found");
        }

        ContractProviderSupplier newContractSupplier = ContractProviderSupplier.builder()
                .serviceType(contractProviderSupplierRequestDto.getServiceType())
                .serviceDuration(contractProviderSupplierRequestDto.getServiceDuration())
                .serviceName(contractProviderSupplierRequestDto.getServiceName())
                .contractReference(contractProviderSupplierRequestDto.getContractReference())
                .description(contractProviderSupplierRequestDto.getDescription())
                .allocatedLimit(contractProviderSupplierRequestDto.getAllocatedLimit())
                .expenseType(contractProviderSupplierRequestDto.getExpenseType())
                .startDate(contractProviderSupplierRequestDto.getStartDate())
                .endDate(contractProviderSupplierRequestDto.getEndDate())
                .activities(activities)
                .requirements(requirements)
                .providerSupplier(providerSupplier)
                .build();

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(newContractSupplier);

        ContractResponseDto contractResponseDto = ContractResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .startDate(savedContractProviderSupplier.getStartDate())
                .endDate(savedContractProviderSupplier.getEndDate())
                .activities(savedContractProviderSupplier.getActivities())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getFantasyName())
                .build();

        return contractResponseDto;
    }

    @Override
    public Optional<ContractResponseDto> findOne(String id) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(id);

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        ContractResponseDto contractResponseDto = ContractResponseDto.builder()
                .idContract(contractProviderSupplier.getIdContract())
                .serviceType(contractProviderSupplier.getServiceType())
                .serviceDuration(contractProviderSupplier.getServiceDuration())
                .serviceName(contractProviderSupplier.getServiceName())
                .contractReference(contractProviderSupplier.getContractReference())
                .description(contractProviderSupplier.getDescription())
                .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                .expenseType(contractProviderSupplier.getExpenseType())
                .startDate(contractProviderSupplier.getStartDate())
                .endDate(contractProviderSupplier.getEndDate())
                .activities(contractProviderSupplier.getActivities())
                .requirements(contractProviderSupplier.getRequirements())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getFantasyName())
                .build();

        return Optional.of(contractResponseDto);
    }

    @Override
    public Page<ContractResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAll(pageable);

        Page<ContractResponseDto> providerResponseDtoPage = contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceType())
                        .serviceDuration(contractProviderSupplier.getServiceDuration())
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .allocatedLimit(contractProviderSupplier.getAllocatedLimit())
                        .expenseType(contractProviderSupplier.getExpenseType())
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
    public Optional<ContractResponseDto> update(ContractRequestDto contractProviderSupplierRequestDto) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(contractProviderSupplierRequestDto.getIdContract());

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new EntityNotFoundException("Supplier not found"));

        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();

        if (contractProviderSupplierRequestDto.getActivities() != null) {
            activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getActivities());
            if (activities.isEmpty()) {
                throw new EntityNotFoundException("Activities not found");
            }
        }

        if (contractProviderSupplierRequestDto.getRequirements() != null) {
            requirements = requirementRepository.findAllById(contractProviderSupplierRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new EntityNotFoundException("Requirements not found");
            }
        }

        contractProviderSupplier.setServiceType(contractProviderSupplierRequestDto.getServiceType() != null ? contractProviderSupplierRequestDto.getServiceType() : contractProviderSupplier.getServiceType());
        contractProviderSupplier.setServiceDuration(contractProviderSupplierRequestDto.getServiceDuration() != null ? contractProviderSupplierRequestDto.getServiceDuration() : contractProviderSupplier.getServiceDuration());
        contractProviderSupplier.setServiceName(contractProviderSupplierRequestDto.getServiceName() != null ? contractProviderSupplierRequestDto.getServiceName() : contractProviderSupplier.getServiceName());
        contractProviderSupplier.setContractReference(contractProviderSupplierRequestDto.getContractReference() != null ? contractProviderSupplierRequestDto.getContractReference() : contractProviderSupplier.getContractReference());
        contractProviderSupplier.setDescription(contractProviderSupplierRequestDto.getDescription() != null ? contractProviderSupplierRequestDto.getDescription() : contractProviderSupplier.getDescription());
        contractProviderSupplier.setAllocatedLimit(contractProviderSupplierRequestDto.getAllocatedLimit() != null ? contractProviderSupplierRequestDto.getAllocatedLimit() : contractProviderSupplier.getAllocatedLimit());
        contractProviderSupplier.setExpenseType(contractProviderSupplierRequestDto.getExpenseType() != null ? contractProviderSupplierRequestDto.getExpenseType() : contractProviderSupplier.getExpenseType());
        contractProviderSupplier.setStartDate(contractProviderSupplierRequestDto.getStartDate() != null ? contractProviderSupplierRequestDto.getStartDate() : contractProviderSupplier.getStartDate());
        contractProviderSupplier.setEndDate(contractProviderSupplierRequestDto.getEndDate() != null ? contractProviderSupplierRequestDto.getEndDate() : contractProviderSupplier.getEndDate());
        contractProviderSupplier.setActivities(contractProviderSupplierRequestDto.getActivities() != null ? activities : contractProviderSupplier.getActivities());
        contractProviderSupplier.setRequirements(contractProviderSupplierRequestDto.getRequirements() != null ? requirements : contractProviderSupplier.getRequirements());
        contractProviderSupplier.setIsActive(contractProviderSupplierRequestDto.getIsActive() != null ? contractProviderSupplierRequestDto.getIsActive() : contractProviderSupplier.getIsActive());

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(contractProviderSupplier);

        ContractResponseDto contractResponseDto = ContractResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceType())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .startDate(savedContractProviderSupplier.getStartDate())
                .endDate(savedContractProviderSupplier.getEndDate())
                .activities(savedContractProviderSupplier.getActivities())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getIdProvider())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getFantasyName())
                .build();

        return Optional.of(contractResponseDto);
    }

    @Override
    public void delete(String id) {
        contractProviderSupplierRepository.deleteById(id);
    }
}
