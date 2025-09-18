package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.enums.DocumentTypeEnum;
import bl.tech.realiza.domains.enums.PermissionSubTypeEnum;
import bl.tech.realiza.domains.enums.PermissionTypeEnum;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import bl.tech.realiza.exceptions.ForbiddenException;
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
import bl.tech.realiza.services.queue.setup.SetupMessage;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.setup.SetupQueueProducer;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContractProviderSubcontractor;
import bl.tech.realiza.usecases.interfaces.users.security.CrudPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;

@Service
@RequiredArgsConstructor
public class CrudContractProviderSubcontractorImpl implements CrudContractProviderSubcontractor {

    private final ContractProviderSubcontractorRepository contractProviderSubcontractorRepository;
    private final ProviderSubcontractorRepository providerSubcontractorRepository;
    private final ActivityRepository activityRepository;
    private final UserProviderSupplierRepository userProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final ContractRepository contractRepository;
    private final SetupQueueProducer setupQueueProducer;
    private final CrudItemManagement crudItemManagement;
    private final CrudPermission crudPermission;

    @Override
    public ContractSubcontractorResponseDto save(ContractSubcontractorPostRequestDto contractProviderSubcontractorRequestDto) {
        if (JwtService.getAuthenticatedUserId() != null) {
            User user = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElseThrow(() -> new NotFoundException("User not found"));
            if (!crudPermission.hasPermission(user,
                    PermissionTypeEnum.CONTRACT,
                    PermissionSubTypeEnum.CREATE,
                    DocumentTypeEnum.NONE)) {
                throw new ForbiddenException("Not enough permissions");
            }
        } else {
            throw new ForbiddenException("Not authenticated user");
        }
        List<Activity> activities = List.of();

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdContractSupplier())
                .orElseThrow(() -> new NotFoundException("Supplier contract not found"));

        if (!contractProviderSupplier.getSubcontractPermission()) {
            throw new UnprocessableEntityException("Contract can't get subcontracted");
        }

        ProviderSubcontractor newProviderSubcontractor = providerSubcontractorRepository.findByCnpj(contractProviderSubcontractorRequestDto.getProviderDatas().getCnpj())
                .orElse(null);

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
                .subcontractLevel(contractProviderSubcontractorRequestDto.getSubcontractLevel() != null
                        ? contractProviderSubcontractorRequestDto.getSubcontractLevel()
                        : 1)
                .contractProviderSupplier(contractProviderSupplier)
                .activities(activities)
                .providerSubcontractor(newProviderSubcontractor)
                .providerSupplier(providerSupplier)
                .build());

        setupQueueProducer.send(SetupMessage.builder()
                        .type("NEW_CONTRACT_SUBCONTRACTOR")
                        .contractSubcontractorId(savedContractSubcontractor.getIdContract())
                        .activityIds(activities.stream().map(Activity::getIdActivity).toList())
                .build());

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> {
                        auditLogServiceImpl.createAuditLog(
                                savedContractSubcontractor.getIdContract(),
                                CONTRACT,
                                userResponsible.getFullName() + " criou contrato "
                                        + savedContractSubcontractor.getContractReference(),
                                null,
                                null,
                                CREATE,
                                userResponsible.getIdUser());

                        // criar solicitação
                        crudItemManagement.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                                .solicitationType(ItemManagement.SolicitationType.CREATION)
                                .idRequester(userResponsible.getIdUser())
                                .idNewProvider(savedContractSubcontractor.getProviderSubcontractor().getIdProvider())
                                .build());
                    }
            );
        }


        return toContractSubcontractorResponseDto(savedContractSubcontractor);
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> findOne(String id) {
        return Optional.of(toContractSubcontractorResponseDto(contractProviderSubcontractorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found"))));
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAll(Pageable pageable) {
        return contractProviderSubcontractorRepository.findAllByIsActiveIsTrue(pageable)
                .map(this::toContractSubcontractorResponseDto);
    }

    @Override
    public Optional<ContractSubcontractorResponseDto> update(String id, ContractRequestDto contractProviderSubcontractorRequestDto) {
        List<Activity> activities = List.of();

        ContractProviderSubcontractor contractProviderSubcontractor = contractProviderSubcontractorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found"));

        UserProviderSupplier userProviderSupplier = userProviderSupplierRepository.findById(contractProviderSubcontractorRequestDto.getIdProviderSupplier())
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSubcontractorRequestDto.getHse() && !contractProviderSubcontractorRequestDto.getIdActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSubcontractorRequestDto.getIdActivities());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        contractProviderSubcontractor.setServiceName(contractProviderSubcontractorRequestDto.getServiceName() != null
                ? contractProviderSubcontractorRequestDto.getServiceName()
                : contractProviderSubcontractor.getServiceName());
        contractProviderSubcontractor.setSubcontractLevel(contractProviderSubcontractorRequestDto.getSubcontractLevel() != null
                ? contractProviderSubcontractorRequestDto.getSubcontractLevel()
                : contractProviderSubcontractor.getSubcontractLevel());
        contractProviderSubcontractor.setContractReference(contractProviderSubcontractorRequestDto.getContractReference() != null
                ? contractProviderSubcontractorRequestDto.getContractReference()
                : contractProviderSubcontractor.getContractReference());
        contractProviderSubcontractor.setDescription(contractProviderSubcontractorRequestDto.getDescription() != null
                ? contractProviderSubcontractorRequestDto.getDescription()
                : contractProviderSubcontractor.getDescription());
        contractProviderSubcontractor.setResponsible(contractProviderSubcontractorRequestDto.getResponsible() != null
                ? userProviderSupplier
                : contractProviderSubcontractor.getResponsible());
        contractProviderSubcontractor.setExpenseType(contractProviderSubcontractorRequestDto.getExpenseType() != null
                ? contractProviderSubcontractorRequestDto.getExpenseType()
                : contractProviderSubcontractor.getExpenseType());
        contractProviderSubcontractor.setDateStart(contractProviderSubcontractorRequestDto.getStartDate() != null
                ? contractProviderSubcontractorRequestDto.getStartDate()
                : contractProviderSubcontractor.getDateStart());
        contractProviderSubcontractor.setEndDate(contractProviderSubcontractorRequestDto.getEndDate() != null
                ? contractProviderSubcontractorRequestDto.getEndDate()
                : contractProviderSubcontractor.getEndDate());
        contractProviderSubcontractor.setActivities(contractProviderSubcontractorRequestDto.getIdActivities() != null
                ? activities
                : contractProviderSubcontractor.getActivities());

        ContractProviderSubcontractor savedContractSubcontractor = contractProviderSubcontractorRepository.save(contractProviderSubcontractor);

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        savedContractSubcontractor.getIdContract(),
                        CONTRACT,
                        userResponsible.getFullName() + " atualizou contrato "
                                + savedContractSubcontractor.getContractReference(),
                        null,
                        null,
                        UPDATE,
                        userResponsible.getIdUser());
            }
        }

        return Optional.of(toContractSubcontractorResponseDto(savedContractSubcontractor));
    }

    @Override
    public void delete(String id) {
        Contract contract = contractRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Contract not found"));

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLog(
                        contract.getIdContract(),
                        CONTRACT,
                        userResponsible.getFullName() + " deletou contrato "
                                + contract.getContractReference(),
                        null,
                        null,
                        DELETE,
                        userResponsible.getIdUser());
            }
        }
        contractProviderSubcontractorRepository.deleteById(id);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAllBySubcontractor(String idSearch, Pageable pageable) {
        return contractProviderSubcontractorRepository.findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(idSearch, pageable)
                .map(this::toContractSubcontractorResponseDto);
    }

    @Override
    public Page<ContractSubcontractorResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        return contractProviderSubcontractorRepository.findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable)
                .map(this::toContractSubcontractorResponseDto);
    }

    @Override
    public List<ContractSubcontractorResponseDto> findAllByContractSupplier(String contractId) {
        return toContractSubcontractorResponseDto(contractProviderSubcontractorRepository.findAllByContractProviderSupplier_IdContract(contractId));
    }

    private ContractSubcontractorResponseDto toContractSubcontractorResponseDto(ContractProviderSubcontractor contractProviderSubcontractor) {
            return ContractSubcontractorResponseDto.builder()
                    .idContract(contractProviderSubcontractor.getIdContract())
                    .serviceType(contractProviderSubcontractor.getServiceTypeBranch().getIdServiceType())
                    .serviceName(contractProviderSubcontractor.getServiceName())
                    .contractReference(contractProviderSubcontractor.getContractReference())
                    .description(contractProviderSubcontractor.getDescription())
                    .cnpj(contractProviderSubcontractor.getProviderSubcontractor() != null
                            ? contractProviderSubcontractor.getProviderSubcontractor().getCnpj()
                            : null)
                    .corporateName(contractProviderSubcontractor.getProviderSubcontractor() != null
                            ? contractProviderSubcontractor.getProviderSubcontractor().getCorporateName()
                            : null)
                    .responsible(contractProviderSubcontractor.getResponsible() != null
                            ? contractProviderSubcontractor.getResponsible().getFullName()
                            : null)
                    .expenseType(contractProviderSubcontractor.getExpenseType())
                    .subcontractLevel(contractProviderSubcontractor.getSubcontractLevel())
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

    private List<ContractSubcontractorResponseDto> toContractSubcontractorResponseDto(List<ContractProviderSubcontractor> contractProviderSubcontractor) {
            return contractProviderSubcontractor.stream().map(this::toContractSubcontractorResponseDto).toList();
    }
}