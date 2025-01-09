package bl.tech.realiza.usecases.impl.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.gateways.repositories.contracts.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
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
                .service_type(contractProviderSupplierRequestDto.getService_type())
                .service_duration(contractProviderSupplierRequestDto.getService_duration())
                .service_name(contractProviderSupplierRequestDto.getService_name())
                .description(contractProviderSupplierRequestDto.getDescription())
                .allocated_limit(contractProviderSupplierRequestDto.getAllocated_limit())
                .start_date(contractProviderSupplierRequestDto.getStart_date())
                .end_date(contractProviderSupplierRequestDto.getEnd_date())
                .activities(activities)
                .requirements(requirements)
                .providerSupplier(providerSupplier)
                .build();

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(newContractSupplier);

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .id_contract(savedContractProviderSupplier.getId_contract())
                .service_type(savedContractProviderSupplier.getService_type())
                .service_duration(savedContractProviderSupplier.getService_duration())
                .service_name(savedContractProviderSupplier.getService_name())
                .description(savedContractProviderSupplier.getDescription())
                .allocated_limit(savedContractProviderSupplier.getAllocated_limit())
                .start_date(savedContractProviderSupplier.getStart_date())
                .end_date(savedContractProviderSupplier.getEnd_date())
                .activities(savedContractProviderSupplier.getActivities())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getId_provider())
                .build();

        return contractProviderResponseDto;
    }

    @Override
    public Optional<ContractProviderResponseDto> findOne(String id) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(id);

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional.orElseThrow(() -> new RuntimeException("Supplier not found"));

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .id_contract(contractProviderSupplier.getId_contract())
                .service_type(contractProviderSupplier.getService_type())
                .service_duration(contractProviderSupplier.getService_duration())
                .service_name(contractProviderSupplier.getService_name())
                .description(contractProviderSupplier.getDescription())
                .allocated_limit(contractProviderSupplier.getAllocated_limit())
                .start_date(contractProviderSupplier.getStart_date())
                .end_date(contractProviderSupplier.getEnd_date())
                .activities(contractProviderSupplier.getActivities())
                .requirements(contractProviderSupplier.getRequirements())
                .providerSupplier(contractProviderSupplier.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public Page<ContractProviderResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAll(pageable);

        Page<ContractProviderResponseDto> providerResponseDtoPage = contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractProviderResponseDto.builder()
                        .id_contract(contractProviderSupplier.getId_contract())
                        .service_type(contractProviderSupplier.getService_type())
                        .service_duration(contractProviderSupplier.getService_duration())
                        .service_name(contractProviderSupplier.getService_name())
                        .description(contractProviderSupplier.getDescription())
                        .allocated_limit(contractProviderSupplier.getAllocated_limit())
                        .start_date(contractProviderSupplier.getStart_date())
                        .end_date(contractProviderSupplier.getEnd_date())
                        .activities(contractProviderSupplier.getActivities())
                        .requirements(contractProviderSupplier.getRequirements())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier().getId_provider())
                        .build()
        );

        return providerResponseDtoPage;
    }

    @Override
    public Optional<ContractProviderResponseDto> update(ContractProviderSupplierRequestDto contractProviderSupplierRequestDto) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(contractProviderSupplierRequestDto.getId_contract());

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

        contractProviderSupplier.setService_type(contractProviderSupplierRequestDto.getService_type() != null ? contractProviderSupplierRequestDto.getService_type() : contractProviderSupplier.getService_type());
        contractProviderSupplier.setService_duration(contractProviderSupplierRequestDto.getService_duration() != null ? contractProviderSupplierRequestDto.getService_duration() : contractProviderSupplier.getService_duration());
        contractProviderSupplier.setService_name(contractProviderSupplierRequestDto.getService_name() != null ? contractProviderSupplierRequestDto.getService_name() : contractProviderSupplier.getService_name());
        contractProviderSupplier.setDescription(contractProviderSupplierRequestDto.getDescription() != null ? contractProviderSupplierRequestDto.getDescription() : contractProviderSupplier.getDescription());
        contractProviderSupplier.setAllocated_limit(contractProviderSupplierRequestDto.getAllocated_limit() != null ? contractProviderSupplierRequestDto.getAllocated_limit() : contractProviderSupplier.getAllocated_limit());
        contractProviderSupplier.setStart_date(contractProviderSupplierRequestDto.getStart_date() != null ? contractProviderSupplierRequestDto.getStart_date() : contractProviderSupplier.getStart_date());
        contractProviderSupplier.setEnd_date(contractProviderSupplierRequestDto.getEnd_date() != null ? contractProviderSupplierRequestDto.getEnd_date() : contractProviderSupplier.getEnd_date());
        contractProviderSupplier.setActivities(contractProviderSupplierRequestDto.getActivities() != null ? activities : contractProviderSupplier.getActivities());
        contractProviderSupplier.setRequirements(contractProviderSupplierRequestDto.getRequirements() != null ? requirements : contractProviderSupplier.getRequirements());

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(contractProviderSupplier);

        ContractProviderResponseDto contractProviderResponseDto = ContractProviderResponseDto.builder()
                .id_contract(savedContractProviderSupplier.getId_contract())
                .service_type(savedContractProviderSupplier.getService_type())
                .service_duration(savedContractProviderSupplier.getService_duration())
                .service_name(savedContractProviderSupplier.getService_name())
                .description(savedContractProviderSupplier.getDescription())
                .allocated_limit(savedContractProviderSupplier.getAllocated_limit())
                .start_date(savedContractProviderSupplier.getStart_date())
                .end_date(savedContractProviderSupplier.getEnd_date())
                .activities(savedContractProviderSupplier.getActivities())
                .requirements(savedContractProviderSupplier.getRequirements())
                .providerSupplier(savedContractProviderSupplier.getProviderSupplier().getId_provider())
                .build();

        return Optional.of(contractProviderResponseDto);
    }

    @Override
    public void delete(String id) {
        contractProviderSupplierRepository.deleteById(id);
    }
}
