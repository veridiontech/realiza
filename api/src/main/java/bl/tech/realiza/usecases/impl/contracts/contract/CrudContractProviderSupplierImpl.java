package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.Contract.IsActive;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.ItemManagement;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.exceptions.BadRequestException;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.client.DocumentBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.contract.DocumentContractRepository;
import bl.tech.realiza.gateways.repositories.documents.provider.DocumentProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.users.UserClientRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.requests.contracts.ContractAndSupplierCreateRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractRequestDto;
import bl.tech.realiza.gateways.requests.contracts.ContractSupplierPostRequestDto;
import bl.tech.realiza.gateways.requests.services.itemManagement.ItemManagementProviderRequestDto;
import bl.tech.realiza.gateways.responses.contracts.contract.*;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.gateways.responses.queue.SetupMessage;
import bl.tech.realiza.gateways.responses.users.UserResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.services.queue.SetupAsyncQueueProducer;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContractProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static bl.tech.realiza.domains.contract.Contract.IsActive.*;
import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;
import static bl.tech.realiza.domains.enums.AuditLogTypeEnum.*;
import static bl.tech.realiza.domains.user.User.Role.*;

@Service
@RequiredArgsConstructor
public class CrudContractProviderSupplierImpl implements CrudContractProviderSupplier {

    private final ContractProviderSupplierRepository contractProviderSupplierRepository;
    private final ProviderSupplierRepository providerSupplierRepository;
    private final ActivityRepository activityRepository;
    private final UserClientRepository userClientRepository;
    private final BranchRepository branchRepository;
    private final DocumentBranchRepository documentBranchRepository;
    private final DocumentContractRepository documentContractRepository;
    private final DocumentProviderSupplierRepository documentProviderSupplierRepository;
    private final CrudItemManagement crudItemManagementImpl;
    private final ServiceTypeBranchRepository serviceTypeBranchRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final ContractRepository contractRepository;
    private final SetupAsyncQueueProducer setupQueueProducer;
    private final JwtService jwtService;

    @Override
    public ContractSupplierResponseDto save(ContractSupplierPostRequestDto contractProviderSupplierRequestDto) {
        List<Activity> activities = List.of();

        ProviderSupplier newProviderSupplier = providerSupplierRepository.findByCnpj(contractProviderSupplierRequestDto.getProviderDatas().getCnpj())
                .orElse(null);

        Branch branch = branchRepository.findById(contractProviderSupplierRequestDto.getIdBranch())
                .orElseThrow(() -> new NotFoundException("Branch not found"));

        UserClient responsible = userClientRepository.findById(contractProviderSupplierRequestDto.getIdResponsible())
                .orElseThrow(() -> new NotFoundException("User responsible not found"));

        User requester = userRepository.findById(contractProviderSupplierRequestDto.getIdRequester())
                .orElseThrow(() -> new NotFoundException("User requester not found"));

        ServiceTypeBranch serviceTypeBranch = serviceTypeBranchRepository.findById(contractProviderSupplierRequestDto.getIdServiceType())
                .orElseThrow(() ->  new NotFoundException("Service Type not found"));

        if (contractProviderSupplierRequestDto.getIdActivities() != null
                && !contractProviderSupplierRequestDto.getIdActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getIdActivities());
        }

        if (newProviderSupplier == null) {
            newProviderSupplier = providerSupplierRepository.save(ProviderSupplier.builder()
                    .cnpj(contractProviderSupplierRequestDto.getProviderDatas().getCnpj())
                    .corporateName(contractProviderSupplierRequestDto.getProviderDatas().getCorporateName())
                    .email(contractProviderSupplierRequestDto.getProviderDatas().getEmail())
                    .telephone(contractProviderSupplierRequestDto.getProviderDatas().getTelephone())
                    .branches(List.of(branch))
                    .build());
        } else {
            if (!newProviderSupplier.getIsActive()) {
                throw new IllegalArgumentException("Provider supplier not active");
            }
            List<Branch> newBranches = newProviderSupplier.getBranches();
            if (!newBranches.contains(branch)) {
                newBranches.add(branch);
                newProviderSupplier.setBranches(newBranches);
                newProviderSupplier = providerSupplierRepository.save(newProviderSupplier);
            }
        }

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(ContractProviderSupplier.builder()
                .serviceTypeBranch(serviceTypeBranch)
                .serviceName(contractProviderSupplierRequestDto.getServiceName())
                .contractReference(contractProviderSupplierRequestDto.getContractReference())
                .description(contractProviderSupplierRequestDto.getDescription())
                .dateStart(contractProviderSupplierRequestDto.getDateStart())
                .labor(contractProviderSupplierRequestDto.getLabor())
                .hse(contractProviderSupplierRequestDto.getHse())
                .responsible(responsible)
                .expenseType(contractProviderSupplierRequestDto.getExpenseType())
                .subcontractPermission(contractProviderSupplierRequestDto.getSubcontractPermission())
                .activities(activities)
                .providerSupplier(newProviderSupplier)
                .branch(branch)
                .build());

        UserClient userClient = userClientRepository.findById(savedContractProviderSupplier.getResponsible().getIdUser())
                .orElseThrow(() -> new NotFoundException("User not found"));

        userClient.getContractsAccess().add(savedContractProviderSupplier);
        userClientRepository.save(userClient);

        setupQueueProducer.sendSetup(new SetupMessage("NEW_CONTRACT_SUPPLIER",
                null,
                null,
                savedContractProviderSupplier.getIdContract(),
                null,
                null,
                activities.stream().map(Activity::getIdActivity).toList(),
                null,
                null,
                null,
                null,
                null,
                Activity.Risk.LOW,
                ServiceType.Risk.LOW));

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                        savedContractProviderSupplier.getIdContract(),
                        CONTRACT,
                        userResponsible.getEmail() + " criou contrato " + savedContractProviderSupplier.getContractReference(),
                            null,
                            CREATE,
                        userResponsible.getIdUser()));
        }

        // criar solicitação
        crudItemManagementImpl.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                        .solicitationType(ItemManagement.SolicitationType.CREATION)
                        .idRequester(requester.getIdUser())
                        .idNewProvider(newProviderSupplier.getIdProvider())
                .build());

        return ContractSupplierResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceTypeBranch() != null
                        ? savedContractProviderSupplier.getServiceTypeBranch().getIdServiceType()
                        : null)
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .idResponsible(savedContractProviderSupplier.getResponsible() != null
                        ? savedContractProviderSupplier.getResponsible().getIdUser()
                        : null)
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .dateStart(savedContractProviderSupplier.getDateStart())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activities(savedContractProviderSupplier.getActivities()
                        .stream().map(Activity::getIdActivity).toList())
                .isActive(savedContractProviderSupplier.getIsActive())
                .idSupplier(savedContractProviderSupplier.getProviderSupplier() != null
                        ? savedContractProviderSupplier.getProviderSupplier().getIdProvider()
                        : null)
                .nameSupplier(savedContractProviderSupplier.getProviderSupplier() != null
                        ? savedContractProviderSupplier.getProviderSupplier().getCorporateName()
                        : null)
                .idBranch(savedContractProviderSupplier.getBranch() != null
                        ? savedContractProviderSupplier.getBranch().getIdBranch()
                        : null)
                .nameBranch(savedContractProviderSupplier.getBranch() != null
                        ? savedContractProviderSupplier.getBranch().getName()
                        : null)
                .build();
    }

    @Override
    public Optional<ContractResponseDto> findOne(String id) {
        Optional<ContractProviderSupplier> providerSupplierOptional = contractProviderSupplierRepository.findById(id);

        ContractProviderSupplier contractProviderSupplier = providerSupplierOptional
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        return getContractResponseDto(contractProviderSupplier, contractProviderSupplier);
    }

    @Override
    public Page<ContractResponseDto> findAll(Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByIsActiveIn(List.of(ATIVADO, SUSPENSO), pageable);

        return contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceTypeBranch() != null ? contractProviderSupplier.getServiceTypeBranch().getIdServiceType() : null)
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .responsible(contractProviderSupplier.getResponsible() != null
                                ? contractProviderSupplier.getResponsible().getFirstName()
                                + " "
                                + contractProviderSupplier.getResponsible().getSurname()
                                : null)
                        .expenseType(contractProviderSupplier.getExpenseType())
                        .dateStart(contractProviderSupplier.getDateStart())
                        .endDate(contractProviderSupplier.getEndDate())
                        .finished(contractProviderSupplier.getFinished())
                        .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
                        .isActive(contractProviderSupplier.getIsActive())
                        .activities(contractProviderSupplier.getActivities()
                                .stream().map(Activity::getIdActivity).toList())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getIdProvider() : null)
                        .providerSupplierName(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getCorporateName() : null)
                        .branch(contractProviderSupplier.getBranch() != null ? contractProviderSupplier.getBranch().getIdBranch() : null)
                        .branchName(contractProviderSupplier.getBranch() != null ? contractProviderSupplier.getBranch().getName() : null)
                        .build()
        );
    }

    @Override
    public Optional<ContractResponseDto> update(String id, ContractRequestDto contractProviderSupplierRequestDto) {
        ServiceTypeBranch serviceTypeBranch = null;
        UserClient userClient = null;

        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        if (contractProviderSupplierRequestDto.getIdServiceType() != null && !contractProviderSupplierRequestDto.getIdServiceType().isEmpty()) {
            serviceTypeBranch = serviceTypeBranchRepository.findById(contractProviderSupplierRequestDto.getIdServiceType())
                    .orElseThrow(() -> new NotFoundException("Service type not found"));
        }

        if (contractProviderSupplierRequestDto.getResponsible() != null && !contractProviderSupplierRequestDto.getResponsible().isEmpty()) {
            userClient = userClientRepository.findById(contractProviderSupplierRequestDto.getResponsible())
                    .orElseThrow(() -> new NotFoundException("User not found"));
        }

        List<Activity> activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getIdActivities());

        contractProviderSupplier.setServiceTypeBranch(contractProviderSupplierRequestDto.getIdServiceType() != null ? serviceTypeBranch : contractProviderSupplier.getServiceTypeBranch());
        contractProviderSupplier.setServiceName(contractProviderSupplierRequestDto.getServiceName() != null ? contractProviderSupplierRequestDto.getServiceName() : contractProviderSupplier.getServiceName());
        contractProviderSupplier.setContractReference(contractProviderSupplierRequestDto.getContractReference() != null ? contractProviderSupplierRequestDto.getContractReference() : contractProviderSupplier.getContractReference());
        contractProviderSupplier.setDescription(contractProviderSupplierRequestDto.getDescription() != null ? contractProviderSupplierRequestDto.getDescription() : contractProviderSupplier.getDescription());
        contractProviderSupplier.setResponsible(contractProviderSupplierRequestDto.getResponsible() != null ? userClient : contractProviderSupplier.getResponsible());
        contractProviderSupplier.setExpenseType(contractProviderSupplierRequestDto.getExpenseType() != null ? contractProviderSupplierRequestDto.getExpenseType() : contractProviderSupplier.getExpenseType());
        contractProviderSupplier.setDateStart(contractProviderSupplierRequestDto.getStartDate() != null ? contractProviderSupplierRequestDto.getStartDate() : contractProviderSupplier.getDateStart());
        contractProviderSupplier.setEndDate(contractProviderSupplierRequestDto.getEndDate() != null ? contractProviderSupplierRequestDto.getEndDate() : contractProviderSupplier.getEndDate());
        contractProviderSupplier.setActivities(!activities.isEmpty() ? activities : contractProviderSupplier.getActivities());

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(contractProviderSupplier);

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                        savedContractProviderSupplier.getIdContract(),
                        CONTRACT,
                        userResponsible.getEmail() + " atualizou contrato " + savedContractProviderSupplier.getContractReference(),
                        null,
                        UPDATE,
                        userResponsible.getIdUser()));
        }

        return getContractResponseDto(contractProviderSupplier, savedContractProviderSupplier);
    }

    @NotNull
    private Optional<ContractResponseDto> getContractResponseDto(ContractProviderSupplier contractProviderSupplier, ContractProviderSupplier savedContractProviderSupplier) {
        ContractResponseDto contractResponseDto = ContractResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceTypeBranch() != null ? savedContractProviderSupplier.getServiceTypeBranch().getIdServiceType() : null)
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .responsible(savedContractProviderSupplier.getResponsible() != null
                        ? savedContractProviderSupplier.getResponsible().getFirstName()
                        + " " + savedContractProviderSupplier.getResponsible().getSurname()
                        : null)
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .dateStart(savedContractProviderSupplier.getDateStart())
                .endDate(savedContractProviderSupplier.getEndDate())
                .finished(savedContractProviderSupplier.getFinished())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .isActive(savedContractProviderSupplier.getIsActive())
                .activities(contractProviderSupplier.getActivities()
                        .stream().map(Activity::getIdActivity).toList())
                .providerSupplier(contractProviderSupplier.getProviderSupplier() != null
                        ? contractProviderSupplier.getProviderSupplier().getIdProvider()
                        : null)
                .providerSupplierName(contractProviderSupplier.getProviderSupplier() != null
                        ? contractProviderSupplier.getProviderSupplier().getCorporateName()
                        : null)
                .branch(contractProviderSupplier.getBranch() != null
                        ? contractProviderSupplier.getBranch().getIdBranch()
                        : null)
                .branchName(contractProviderSupplier.getBranch() != null
                        ? contractProviderSupplier.getBranch().getName()
                        : null)
                .activities(contractProviderSupplier.getActivities() != null
                        ? contractProviderSupplier.getActivities().stream().map(Activity::getIdActivity).toList()
                        : null)
                .build();

        return Optional.of(contractResponseDto);
    }

    @Override
    public void delete(String id) {
        Contract contract = contractRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Contract not found"));
        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                        contract.getIdContract(),
                        CONTRACT,
                        userResponsible.getEmail() + " deletou contrato " + contract.getContractReference(),
                        null,
                        DELETE,
                        userResponsible.getIdUser()));
        }
        contractProviderSupplierRepository.deleteById(id);
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplier(String idSearch, List<IsActive> isActive, Pageable pageable) {
        if (isActive == null || isActive.isEmpty()) {
            isActive = List.of(ATIVADO);
        }
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActiveIn(idSearch, isActive, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @Override
    public Page<ContractResponseDto> findAllByClient(String idSearch, List<IsActive> isActive, Pageable pageable) {
        if (isActive == null || isActive.isEmpty()) {
            isActive = List.of(ATIVADO);
        }
        Page<ContractProviderSupplier> contractProviderSupplierPage = null;
        UserResponseDto requester = jwtService.extractAllClaims(jwtService.getTokenFromRequest());
        if (requester.getAdmin()
                || requester.getRole().equals(ROLE_REALIZA_BASIC)
                || requester.getRole().equals(ROLE_REALIZA_PLUS)) {
            contractProviderSupplierPage = contractProviderSupplierRepository.findAllByBranch_IdBranchAndIsActiveInAndProviderSupplier_IsActive(idSearch, isActive, true, pageable);
        } else {
            if (!requester.getBranchAccess().contains(idSearch)) {
                return new PageImpl<>(Collections.emptyList(), pageable, 0);
            }
            List<ContractProviderSupplier> contractProviderSuppliers = contractProviderSupplierRepository.findAllById(requester.getContractAccess());
            List<ContractProviderSupplier> filteredContracts = contractProviderSuppliers.stream()
                    .filter(contract -> contract.getBranch().getIdBranch().equals(idSearch))
                    .sorted(Comparator.comparing(Contract::getContractReference))
                    .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());
            contractProviderSupplierPage = new PageImpl<>(filteredContracts, pageable, filteredContracts.size());
        }

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplierAndBranch(String idSupplier, String idBranch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByBranch_IdBranchAndProviderSupplier_IdProviderAndIsActiveIn(idBranch,idSupplier, List.of(ATIVADO,SUSPENSO), pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @NotNull
    private Page<ContractResponseDto> getContractResponseDtos(Page<ContractProviderSupplier> contractProviderSupplierPage) {

        return contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceTypeBranch() != null
                                ? contractProviderSupplier.getServiceTypeBranch().getIdServiceType()
                                : null)
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .expenseType(contractProviderSupplier.getExpenseType())
                        .dateStart(contractProviderSupplier.getDateStart())
                        .endDate(contractProviderSupplier.getEndDate())
                        .finished(contractProviderSupplier.getFinished())
                        .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
                        .isActive(contractProviderSupplier.getIsActive())
                        .responsible(contractProviderSupplier.getResponsible() != null
                                ? (contractProviderSupplier.getResponsible().getFirstName() + (contractProviderSupplier.getResponsible().getSurname() != null
                                    ? (" " + contractProviderSupplier.getResponsible().getSurname())
                                    : ""))
                                : null)
                        .activities(contractProviderSupplier.getActivities()
                                .stream().map(Activity::getIdActivity).toList())
                        .providerSupplier(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getIdProvider() : null)
                        .providerSupplierCnpj(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getCnpj() : null)
                        .providerSupplierName(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getCorporateName() : null)
                        .branch(contractProviderSupplier.getBranch() != null ? contractProviderSupplier.getBranch().getIdBranch() : null)
                        .branchName(contractProviderSupplier.getBranch() != null ? contractProviderSupplier.getBranch().getName() : null)
                        .build()
        );
    }

    @Override
    public ContractAndSupplierCreateResponseDto saveContractAndSupplier(ContractAndSupplierCreateRequestDto contractAndSupplierCreateRequestDto) {
        List<Activity> activities;

        if (contractAndSupplierCreateRequestDto.getIdBranch() == null || contractAndSupplierCreateRequestDto.getIdBranch().isEmpty()) {
            throw new BadRequestException("Invalid branches");
        }
        Branch branch = branchRepository.findById(contractAndSupplierCreateRequestDto.getIdBranch())
                .orElseThrow(() -> new NotFoundException("Branch not found"));
        List<Branch> branches = new ArrayList<>();
        branches.add(branch);

        List<DocumentBranch> documentBranch = documentBranchRepository
                .findAllByBranch_IdBranchAndIsActiveIsTrue(contractAndSupplierCreateRequestDto.getIdBranch());
        List<DocumentMatrix> documentMatrixList = documentBranch.stream()
                .map(DocumentBranch::getDocumentMatrix)
                .toList();

        ProviderSupplier newProviderSupplier = ProviderSupplier.builder()
                .cnpj(contractAndSupplierCreateRequestDto.getCnpj())
                .tradeName(contractAndSupplierCreateRequestDto.getTradeName())
                .corporateName(contractAndSupplierCreateRequestDto.getCorporateName())
                .email(contractAndSupplierCreateRequestDto.getEmail())
                .cep(contractAndSupplierCreateRequestDto.getCep())
                .state(contractAndSupplierCreateRequestDto.getState())
                .city(contractAndSupplierCreateRequestDto.getCity())
                .address(contractAndSupplierCreateRequestDto.getAddress())
                .number(contractAndSupplierCreateRequestDto.getNumber())
                .branches(branches)
                .build();

        ProviderSupplier savedProviderSupplier = providerSupplierRepository.save(newProviderSupplier);

        List<DocumentProviderSupplier> documentProviderSuppliers = documentMatrixList.stream()
                .map(docMatrix -> DocumentProviderSupplier.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .providerSupplier(savedProviderSupplier)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentProviderSupplierRepository.saveAll(documentProviderSuppliers);


        UserClient userClient = userClientRepository.findById(contractAndSupplierCreateRequestDto.getIdResponsible())
                .orElseThrow(() -> new NotFoundException("User not found"));

        activities = activityRepository.findAllById(contractAndSupplierCreateRequestDto.getIdActivityList());

        documentMatrixList = documentBranch.stream()
                .map(DocumentBranch::getDocumentMatrix)
                .toList();

        ContractProviderSupplier newContractSupplier = ContractProviderSupplier.builder()
                .serviceDuration(contractAndSupplierCreateRequestDto.getServiceDuration())
                .serviceName(contractAndSupplierCreateRequestDto.getServiceName())
                .contractReference(contractAndSupplierCreateRequestDto.getContractReference())
                .description(contractAndSupplierCreateRequestDto.getDescription())
                .allocatedLimit(contractAndSupplierCreateRequestDto.getAllocatedLimit())
                .responsible(userClient)
                .expenseType(contractAndSupplierCreateRequestDto.getExpenseType())
                .dateStart(contractAndSupplierCreateRequestDto.getStartDate())
                .endDate(contractAndSupplierCreateRequestDto.getEndDate())
                .subcontractPermission(contractAndSupplierCreateRequestDto.getSubcontractPermission())
                .providerSupplier(savedProviderSupplier)
                .branch(branch)
                .activities(activities)
                .build();

        ContractProviderSupplier savedContractProviderSupplier = contractProviderSupplierRepository.save(newContractSupplier);

        List<DocumentContract> documentContractProviderSuppliers = documentMatrixList.stream()
                .map(docMatrix -> DocumentContract.builder()
                        .title(docMatrix.getName())
                        .status(Document.Status.PENDENTE)
                        .contract(savedContractProviderSupplier)
                        .documentMatrix(docMatrix)
                        .build())
                .collect(Collectors.toList());

        documentContractRepository.saveAll(documentContractProviderSuppliers);

        return ContractAndSupplierCreateResponseDto.builder()
                .idProviderSupplier(savedProviderSupplier.getIdProvider())
                .cnpj(savedProviderSupplier.getCnpj())
                .tradeName(savedProviderSupplier.getTradeName())
                .corporateName(savedProviderSupplier.getCorporateName())
                .email(savedProviderSupplier.getEmail())
                .cep(savedProviderSupplier.getCep())
                .state(savedProviderSupplier.getState())
                .city(savedProviderSupplier.getCity())
                .address(savedProviderSupplier.getAddress())
                .number(savedProviderSupplier.getNumber())
                .branches(savedProviderSupplier.getBranches().stream().map(
                                savedBranch -> ProviderResponseDto.BranchDto.builder()
                                        .idBranch(savedBranch.getIdBranch())
                                        .nameBranch(savedBranch.getName())
                                        .build())
                        .collect(Collectors.toList()))
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .allocatedLimit(savedContractProviderSupplier.getAllocatedLimit())
                .responsible(savedContractProviderSupplier.getResponsible() != null
                        ? savedContractProviderSupplier.getResponsible().getFirstName()
                        + " "
                        + savedContractProviderSupplier.getResponsible().getSurname()
                        : null)
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .startDate(savedContractProviderSupplier.getDateStart())
                .endDate(savedContractProviderSupplier.getEndDate())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activity(savedContractProviderSupplier.getActivities() != null
                        ? savedContractProviderSupplier.getActivities().stream().map(Activity::getIdActivity).toList()
                        : null)
                .providerSupplierName(savedContractProviderSupplier.getProviderSupplier().getCorporateName())
                .idBranch(savedContractProviderSupplier.getBranch().getIdBranch())
                .branchName(savedContractProviderSupplier.getBranch().getName())
                .build();
    }

    @Override
    public List<ContractSupplierPermissionResponseDto> findAllByBranchAndSubcontractPermission(String idBranch) {
        List<ContractProviderSupplier> contractProviderSuppliers = contractProviderSupplierRepository.findAllByBranch_IdBranchAndIsActiveAndSubcontractPermissionIsTrue(idBranch, ATIVADO);
        return contractProviderSuppliers.stream().map(
                contractProviderSupplier -> ContractSupplierPermissionResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .providerSupplierName(contractProviderSupplier.getProviderSupplier() != null
                                ? contractProviderSupplier.getProviderSupplier().getTradeName()
                                : null)
                        .build()
        )
                .sorted(Comparator.comparing(ContractSupplierPermissionResponseDto::getContractReference, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    @Override
    public ContractResponsibleResponseDto findAllByResponsible(String responsibleId) {
        userClientRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        List<UserClient> responsibleList = userClientRepository.findAllByBranch_IdBranchAndRoleAndProfile_ManagerIsTrue(responsibleId, ROLE_CLIENT_MANAGER)
                .stream().sorted(Comparator.comparing(User::getFullName)).toList();
        List<ContractProviderSupplier> contractProviderSupplierList = contractProviderSupplierRepository.findAllByResponsible_IdUser(responsibleId)
                .stream().sorted(Comparator.comparing(Contract::getContractReference)).toList();

        return ContractResponsibleResponseDto.builder()
                .contracts(
                        contractProviderSupplierList
                                .stream()
                                .map(
                                        contractProviderSupplier -> ContractResponsibleResponseDto.ContractResponsibleInfosResponseDto.builder()
                                                .contractId(contractProviderSupplier.getIdContract())
                                                .contractReference(contractProviderSupplier.getContractReference())
                                                .responsibleId(contractProviderSupplier.getResponsible() != null
                                                        ? contractProviderSupplier.getResponsible().getIdUser()
                                                        : null)
                                                .responsibleFullName(contractProviderSupplier.getResponsible() != null
                                                        ? contractProviderSupplier.getResponsible().getFullName()
                                                        : null)
                                                .build()
                                ).toList()
                )
                .responsibleList(
                        responsibleList
                                .stream()
                                .map(
                                        userClient -> ContractResponsibleResponseDto.ResponsibleResponseDto.builder()
                                                .responsibleId(userClient.getIdUser())
                                                .responsibleFullName(userClient.getFullName())
                                                .build()
                                ).toList())
                .build();
    }

    @Override
    public String updateResponsible(String contractId, String responsibleId) {
        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(contractId)
                .orElseThrow(() -> new NotFoundException("Contract not found"));
        UserClient userClient = userClientRepository.findById(responsibleId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        String oldResponsibleFullName = contractProviderSupplier.getResponsible().getFullName();
        String newResponsibleFullName = userClient.getFullName();

        contractProviderSupplier.setResponsible(userClient);
        contractProviderSupplierRepository.save(contractProviderSupplier);

        if (JwtService.getAuthenticatedUserId() != null) {
            userRepository.findById(JwtService.getAuthenticatedUserId()).ifPresent(
                    userResponsible -> auditLogServiceImpl.createAuditLog(
                            contractProviderSupplier.getIdContract(),
                            CONTRACT,
                            userResponsible.getEmail() + " atualizou contrato " + contractProviderSupplier.getContractReference(),
                            "Mudou o responsável de " + oldResponsibleFullName
                                    + " para " + newResponsibleFullName,
                            UPDATE,
                            userResponsible.getIdUser()));
        }

        return "Responsible updated successfully";
    }
}
