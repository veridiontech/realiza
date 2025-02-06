package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
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
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;

    @Override
    public ContractResponseDto save(ContractRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();
        ProviderSubcontractor providerSubcontractor = null;

        Optional<ContractProviderSupplier> contractProviderSupplierOptional = contractProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getSupplierContractId());
        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier Contract not found"));

        if (!contractProviderSupplier.getSubcontractPermission()) {
            throw new UnprocessableEntityException("Contract can't get subcontracted");
        }

        if (contractProviderSubcontractorRequestDto.getProviderSubcontractor() != null) {
            Optional<ProviderSubcontractor> providerSubcontractorOptional = providerSubcontractorRepository.findById(contractProviderSubcontractorRequestDto.getProviderSubcontractor());
            providerSubcontractor = providerSubcontractorOptional.orElseThrow(() -> new NotFoundException("Subcontractor not found"));
        } else {
            ProviderSubcontractor newSubcontractor = ProviderSubcontractor.builder()
                    .cnpj(contractProviderSubcontractorRequestDto.getCnpj())
                    .build();
            providerSubcontractor = providerSubcontractorRepository.save(newSubcontractor);
        }

        Optional<UserProviderSupplier> userProviderSupplierOptional = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getProviderSupplier());
        UserProviderSupplier userProviderSupplier = userProviderSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        Optional<ProviderSupplier> providerSupplierOptional = providerSupplierRepository.findById(contractProviderSubcontractorRequestDto.getProviderSupplier());
        ProviderSupplier providerSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSubcontractorRequestDto.getActivities() != null && !contractProviderSubcontractorRequestDto.getActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getActivities());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        if (contractProviderSubcontractorRequestDto.getRequirements() != null && !contractProviderSubcontractorRequestDto.getRequirements().isEmpty()) {
            requirements = requirementRepository.findAllById(contractProviderSubcontractorRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new NotFoundException("Requirements not found");
            }
        }

        ContractProviderSubcontractor newContractSubcontractor = ContractProviderSubcontractor.builder()
                .serviceType(contractProviderSubcontractorRequestDto.getServiceType())
                .serviceDuration(contractProviderSubcontractorRequestDto.getServiceDuration())
                .serviceName(contractProviderSubcontractorRequestDto.getServiceName())
                .contractReference(contractProviderSubcontractorRequestDto.getContractReference())
                .description(contractProviderSubcontractorRequestDto.getDescription())
                .allocatedLimit(contractProviderSubcontractorRequestDto.getAllocatedLimit())
                .responsible(userProviderSupplier)
                .expenseType(contractProviderSubcontractorRequestDto.getExpenseType())
                .startDate(contractProviderSubcontractorRequestDto.getStartDate())
                .endDate(contractProviderSubcontractorRequestDto.getEndDate())
                .contractProviderSupplier(contractProviderSupplier)
                .activities(activities)
                .requirements(requirements)
                .providerSubcontractor(providerSubcontractor)
                .providerSupplier(providerSupplier)
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
                .responsible(savedContractSubcontractor.getResponsible().getIdUser())
                .expenseType(savedContractSubcontractor.getExpenseType())
                .startDate(savedContractSubcontractor.getStartDate())
                .endDate(savedContractSubcontractor.getEndDate())
                .activities(savedContractSubcontractor.getActivities())
                .requirements(savedContractSubcontractor.getRequirements())
                .contractSupplierId(savedContractSubcontractor.getContractProviderSupplier().getIdContract())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractorName(savedContractSubcontractor.getProviderSubcontractor().getCorporateName())
                .providerSupplier(savedContractSubcontractor.getProviderSupplier().getIdProvider())
                .providerSupplierName(savedContractSubcontractor.getProviderSupplier().getCorporateName())
                .build();

        return contractSubcontractorResponse;
    }

    @Override
    public Optional<ContractResponseDto> findOne(String id) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new NotFoundException("Contract not found"));

        ContractResponseDto contractProviderResponseDto = ContractResponseDto.builder()
                .idContract(contractProviderSubcontractor.getIdContract())
                .serviceType(contractProviderSubcontractor.getServiceType())
                .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                .serviceName(contractProviderSubcontractor.getServiceName())
                .contractReference(contractProviderSubcontractor.getContractReference())
                .description(contractProviderSubcontractor.getDescription())
                .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
                .responsible(contractProviderSubcontractor.getResponsible().getIdUser())
                .expenseType(contractProviderSubcontractor.getExpenseType())
                .startDate(contractProviderSubcontractor.getStartDate())
                .endDate(contractProviderSubcontractor.getEndDate())
                .activities(contractProviderSubcontractor.getActivities())
                .requirements(contractProviderSubcontractor.getRequirements())
                .contractSupplierId(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractorName(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
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
                        .responsible(contractProviderSubcontractor.getResponsible().getIdUser())
                        .expenseType(contractProviderSubcontractor.getExpenseType())
                        .startDate(contractProviderSubcontractor.getStartDate())
                        .endDate(contractProviderSubcontractor.getEndDate())
                        .activities(contractProviderSubcontractor.getActivities())
                        .requirements(contractProviderSubcontractor.getRequirements())
                        .contractSupplierId(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                        .providerSubcontractorName(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                        .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                        .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                        .build()
        );

        return contractProviderResponseDtoPage;
    }

    @Override
    public Optional<ContractResponseDto> update(String id, ContractRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();

        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);
        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional.orElseThrow(() -> new NotFoundException("Contract not found"));

        Optional<UserProviderSupplier> providerSupplierOptional = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getProviderSupplier());
        UserProviderSupplier userProviderSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSubcontractorRequestDto.getActivities() != null && !contractProviderSubcontractorRequestDto.getActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getActivities());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        if (contractProviderSubcontractorRequestDto.getRequirements() != null && !contractProviderSubcontractorRequestDto.getRequirements().isEmpty()) {
            requirements = requirementRepository.findAllById(contractProviderSubcontractorRequestDto.getRequirements());
            if (requirements.isEmpty()) {
                throw new NotFoundException("Requirements not found");
            }
        }

        contractProviderSubcontractor.setServiceType(contractProviderSubcontractorRequestDto.getServiceType() != null ? contractProviderSubcontractorRequestDto.getServiceType() : contractProviderSubcontractor.getServiceType());
        contractProviderSubcontractor.setServiceDuration(contractProviderSubcontractorRequestDto.getServiceDuration() != null ? contractProviderSubcontractorRequestDto.getServiceDuration() : contractProviderSubcontractor.getServiceDuration());
        contractProviderSubcontractor.setServiceName(contractProviderSubcontractorRequestDto.getServiceName() != null ? contractProviderSubcontractorRequestDto.getServiceName() : contractProviderSubcontractor.getServiceName());
        contractProviderSubcontractor.setContractReference(contractProviderSubcontractorRequestDto.getContractReference() != null ? contractProviderSubcontractorRequestDto.getContractReference() : contractProviderSubcontractor.getContractReference());
        contractProviderSubcontractor.setDescription(contractProviderSubcontractorRequestDto.getDescription() != null ? contractProviderSubcontractorRequestDto.getDescription() : contractProviderSubcontractor.getDescription());
        contractProviderSubcontractor.setAllocatedLimit(contractProviderSubcontractorRequestDto.getAllocatedLimit() != null ? contractProviderSubcontractorRequestDto.getAllocatedLimit() : contractProviderSubcontractor.getAllocatedLimit());
        contractProviderSubcontractor.setResponsible(contractProviderSubcontractorRequestDto.getResponsible() != null ? userProviderSupplier : contractProviderSubcontractor.getResponsible());
        contractProviderSubcontractor.setExpenseType(contractProviderSubcontractorRequestDto.getExpenseType() != null ? contractProviderSubcontractorRequestDto.getExpenseType() : contractProviderSubcontractor.getExpenseType());
        contractProviderSubcontractor.setStartDate(contractProviderSubcontractorRequestDto.getStartDate() != null ? contractProviderSubcontractorRequestDto.getStartDate() : contractProviderSubcontractor.getStartDate());
        contractProviderSubcontractor.setEndDate(contractProviderSubcontractorRequestDto.getEndDate() != null ? contractProviderSubcontractorRequestDto.getEndDate() : contractProviderSubcontractor.getEndDate());
        contractProviderSubcontractor.setActivities(contractProviderSubcontractorRequestDto.getActivities() != null ? activities : contractProviderSubcontractor.getActivities());
        contractProviderSubcontractor.setRequirements(contractProviderSubcontractorRequestDto.getRequirements() != null ? requirements : contractProviderSubcontractor.getRequirements());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(contractProviderSubcontractor);

        ContractResponseDto contractSubcontractorResponse = ContractResponseDto.builder()
                .idContract(savedContractSubcontractor.getIdContract())
                .serviceType(savedContractSubcontractor.getServiceType())
                .serviceDuration(savedContractSubcontractor.getServiceDuration())
                .serviceName(savedContractSubcontractor.getServiceName())
                .contractReference(savedContractSubcontractor.getContractReference())
                .description(savedContractSubcontractor.getDescription())
                .allocatedLimit(savedContractSubcontractor.getAllocatedLimit())
                .responsible(savedContractSubcontractor.getResponsible().getIdUser())
                .expenseType(savedContractSubcontractor.getExpenseType())
                .startDate(savedContractSubcontractor.getStartDate())
                .endDate(savedContractSubcontractor.getEndDate())
                .activities(savedContractSubcontractor.getActivities())
                .requirements(savedContractSubcontractor.getRequirements())
                .contractSupplierId(savedContractSubcontractor.getContractProviderSupplier().getIdContract())
                .providerSubcontractor(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                .providerSubcontractorName(savedContractSubcontractor.getProviderSubcontractor().getCorporateName())
                .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                .build();

        return Optional.of(contractSubcontractorResponse);
    }

    @Override
    public void delete(String id) {
        contractProviderSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ContractResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProvider(idSearch, pageable);

        Page<ContractResponseDto> contractProviderResponseDtoPage = contractProviderSubcontractorPage.map(
                contractProviderSubcontractor -> ContractResponseDto.builder()
                        .idContract(contractProviderSubcontractor.getIdContract())
                        .serviceType(contractProviderSubcontractor.getServiceType())
                        .serviceDuration(contractProviderSubcontractor.getServiceDuration())
                        .serviceName(contractProviderSubcontractor.getServiceName())
                        .contractReference(contractProviderSubcontractor.getContractReference())
                        .description(contractProviderSubcontractor.getDescription())
                        .allocatedLimit(contractProviderSubcontractor.getAllocatedLimit())
                        .expenseType(contractProviderSubcontractor.getExpenseType())
                        .startDate(contractProviderSubcontractor.getStartDate())
                        .endDate(contractProviderSubcontractor.getEndDate())
                        .activities(contractProviderSubcontractor.getActivities())
                        .requirements(contractProviderSubcontractor.getRequirements())
                        .contractSupplierId(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                        .providerSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                        .providerSubcontractorName(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                        .providerSupplier(contractProviderSubcontractor.getProviderSupplier().getIdProvider())
                        .providerSupplierName(contractProviderSubcontractor.getProviderSupplier().getCorporateName())
                        .build()
        );

        return contractProviderResponseDtoPage;
    }
}