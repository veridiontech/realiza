package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.exceptions.UnprocessableEntityException;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSubcontractorRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSubcontractorPostRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractSubcontractorResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContractProviderSubcontractor;
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
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final CrudItemManagement crudItemManagement;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final ContractRepository contractRepository;
    private final SetupAsyncQueueProducer setupQueueProducer;

    @Override
    public ContractSubcontractorResponseDto save(ContractSubcontractorPostRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdContractSupplier())
                .orElseThrow(() -> new NotFoundException("Supplier contract not found"));

        if (!contractProviderSupplier.getSubcontractPermission()) {
            throw new UnprocessableEntityException("Contract can't get subcontracted");
        }

        ProviderSubcontractor newProviderSubcontractor = providerSubcontractorRepository.findByCnpj(contractProviderSubcontractorRequestDto.getProviderDatas().getCnpj())
                .orElseThrow(null);

        ProviderSupplier providerSupplier = providerSupplierRepository.findById(contractProviderSupplier.getProviderSupplier().getIdProvider())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSubcontractorRequestDto.getIdActivities() != null
                && !contractProviderSubcontractorRequestDto.getIdActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getIdActivities());
        }

        if (newProviderSubcontractor == null) {
            newProviderSubcontractor = providerSubcontractorRepository.save(ProviderSubcontractor.builder()
                    .cnpj(contractProviderSubcontractorRequestDto.getProviderDatas().getCnpj())
                    .corporateName(contractProviderSubcontractorRequestDto.getProviderDatas().getCorporateName())
                    .email(contractProviderSubcontractorRequestDto.getProviderDatas().getEmail())
                    .telephone(contractProviderSubcontractorRequestDto.getProviderDatas().getTelephone())
                    .providerSupplier(providerSupplier)
                    .build());
        }

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(ContractProviderSubcontractor.builder()
                .serviceTypeBranch(contractProviderSupplier.getServiceTypeBranch())
                .serviceName(contractProviderSubcontractorRequestDto.getServiceName())
                .contractReference(contractProviderSubcontractorRequestDto.getContractReference())
                .description(contractProviderSubcontractorRequestDto.getDescription())
                .dateStart(contractProviderSubcontractorRequestDto.getDateStart())
                .labor(contractProviderSubcontractorRequestDto.getLabor())
                .hse(contractProviderSubcontractorRequestDto.getHse())
                .responsible(contractProviderSupplier.getResponsible())
                .expenseType(contractProviderSubcontractorRequestDto.getExpenseType())
                .contractProviderSupplier(contractProviderSupplier)
                .activities(activities)
                .providerSubcontractor(newProviderSubcontractor)
                .providerSupplier(providerSupplier)
                .build());

        setupQueueProducer.sendSetup(new SetupMessage("NEW_CONTRACT_SUBCONTRACT", null, null, null, savedContractSubcontractor, contractProviderSubcontractorRequestDto.getIdActivities()));

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLogContract(
                        savedContractSubcontractor,
                        userResponsible.getEmail() + " created contract " + savedContractSubcontractor.getContractReference(),
                        AuditLogContract.AuditLogContractActions.CREATE,
                        userResponsible));
        }

        // criar solicitação
        crudItemManagement.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                .solicitationType(ItemManagement.SolicitationType.CREATION)
                .idRequester(contractProviderSubcontractorRequestDto.getIdRequester())
                .idNewProvider(newProviderSubcontractor.getIdProvider())
                .build());

        return toContractSubcontractorResponseDtos(savedContractSubcontractor);
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> findOne(String id) {
        Optional<ContractProviderSubcontractor> contractProviderSubcontractorOptional = contractProviderSubcontractorRepository.findById(id);

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorOptional
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        return Optional.of(toContractSubcontractorResponseDtos(contractProviderSubcontractor));
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository
                .findAllByIsActiveIsTrue(pageable);

        return contractProviderSubcontractorPage.map(this::toContractSubcontractorResponseDtos);
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> update(String id, ContractRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        Optional<UserProviderSupplier> providerSupplierOptional = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdProviderSupplier());
        UserProviderSupplier userProviderSupplier = providerSupplierOptional.orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSubcontractorRequestDto.getHse() && !contractProviderSubcontractorRequestDto.getIdActivityList().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getIdActivityList());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        contractProviderSubcontractor.setServiceName(contractProviderSubcontractorRequestDto.getServiceName() != null ? contractProviderSubcontractorRequestDto.getServiceName() : contractProviderSubcontractor.getServiceName());
        contractProviderSubcontractor.setContractReference(contractProviderSubcontractorRequestDto.getContractReference() != null ? contractProviderSubcontractorRequestDto.getContractReference() : contractProviderSubcontractor.getContractReference());
        contractProviderSubcontractor.setDescription(contractProviderSubcontractorRequestDto.getDescription() != null ? contractProviderSubcontractorRequestDto.getDescription() : contractProviderSubcontractor.getDescription());
        contractProviderSubcontractor.setResponsible(contractProviderSubcontractorRequestDto.getResponsible() != null ? userProviderSupplier : contractProviderSubcontractor.getResponsible());
        contractProviderSubcontractor.setExpenseType(contractProviderSubcontractorRequestDto.getExpenseType() != null ? contractProviderSubcontractorRequestDto.getExpenseType() : contractProviderSubcontractor.getExpenseType());
        contractProviderSubcontractor.setDateStart(contractProviderSubcontractorRequestDto.getStartDate() != null ? contractProviderSubcontractorRequestDto.getStartDate() : contractProviderSubcontractor.getDateStart());
        contractProviderSubcontractor.setEndDate(contractProviderSubcontractorRequestDto.getEndDate() != null ? contractProviderSubcontractorRequestDto.getEndDate() : contractProviderSubcontractor.getEndDate());
        contractProviderSubcontractor.setActivities(contractProviderSubcontractorRequestDto.getIdActivityList() != null ? activities : contractProviderSubcontractor.getActivities());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(contractProviderSubcontractor);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogContract(
                        savedContractSubcontractor,
                        userResponsible.getEmail() + " updated contract " + savedContractSubcontractor.getContractReference(),
                        AuditLogContract.AuditLogContractActions.UPDATE,
                        userResponsible);
            }
        }

        return Optional.of(toContractSubcontractorResponseDtos(savedContractSubcontractor));
    }

    @Override
    public void delete(String id) {
        Contract contract = contractRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Contract not found"));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogContract(
                        contract,
                        userResponsible.getEmail() + " deleted contract " + contract.getContractReference(),
                        AuditLogContract.AuditLogContractActions.DELETE,
                        userResponsible);
            }
        }
        contractProviderSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository
                .findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return contractProviderSubcontractorPage.map(this::toContractSubcontractorResponseDtos);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ContractProviderSubcontractor> contractProviderSubcontractorPage = contractProviderSubcontractorRepository
                .findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return contractProviderSubcontractorPage.map(this::toContractSubcontractorResponseDtos);
    }

    private ContractSubcontractorResponseDto toContractSubcontractorResponseDtos(ContractProviderSubcontractor contractProviderSubcontractor) {
            return ContractSubcontractorResponseDto.builder()
                    .idContract(contractProviderSubcontractor.getIdContract())
                    .serviceType(contractProviderSubcontractor.getServiceTypeBranch().getIdServiceType())
                    .serviceName(contractProviderSubcontractor.getServiceName())
                    .contractReference(contractProviderSubcontractor.getContractReference())
                    .description(contractProviderSubcontractor.getDescription())
                    .responsible(contractProviderSubcontractor.getResponsible() != null
                            ? contractProviderSubcontractor.getResponsible().getFirstName()
                            + " "
                            + contractProviderSubcontractor.getResponsible().getSurname()
                            : null)
                    .expenseType(contractProviderSubcontractor.getExpenseType())
                    .dateStart(contractProviderSubcontractor.getDateStart())
                    .finished(contractProviderSubcontractor.getFinished())
                    .isActive(contractProviderSubcontractor.getIsActive())
                    .activities(contractProviderSubcontractor.getActivities()
                            .stream().map(Activity::getIdActivity).toList())
                    .idSupplier(contractProviderSubcontractor.getContractProviderSupplier().getIdContract())
                    .nameSupplier(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                    .idSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getIdProvider())
                    .nameSubcontractor(contractProviderSubcontractor.getProviderSubcontractor().getCorporateName())
                    .build();
    }
}