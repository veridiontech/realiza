package bl.tech.realiza.usecases.impl.contracts.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.contract.activity.ActivityDocuments;
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
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityDocumentRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractProviderSupplierRepository;
import bl.tech.realiza.gateways.repositories.contracts.RequirementRepository;
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
import bl.tech.realiza.gateways.responses.contracts.contract.ContractAndSupplierCreateResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractSupplierPermissionResponseDto;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractSupplierResponseDto;
import bl.tech.realiza.gateways.responses.providers.ProviderResponseDto;
import bl.tech.realiza.services.auth.JwtService;
import bl.tech.realiza.usecases.impl.auditLogs.AuditLogServiceImpl;
import bl.tech.realiza.usecases.interfaces.CrudItemManagement;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import bl.tech.realiza.usecases.interfaces.contracts.contract.CrudContractProviderSupplier;
import bl.tech.realiza.usecases.interfaces.providers.CrudProviderSupplier;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final ActivityDocumentRepository activityDocumentRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogServiceImpl;
    private final ContractRepository contractRepository;

    @Override
    public ContractSupplierResponseDto save(ContractSupplierPostRequestDto contractProviderSupplierRequestDto) {
        List<Activity> activities = List.of();
        List<String> idDocuments = new ArrayList<>(List.of());
        List<DocumentBranch> documentBranch = List.of();
        List<DocumentContract> documentContract = new ArrayList<>(List.of());
        List<DocumentProviderSupplier> documentProviderSupplier = new ArrayList<>(List.of());

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

        if (contractProviderSupplierRequestDto.getHse() && !contractProviderSupplierRequestDto.getIdActivities().isEmpty()) {
            activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getIdActivities());
            if (activities.isEmpty()) {
                throw new NotFoundException("Activities not found");
            }
        }

        activities.forEach(
                activity -> {
                    List<ActivityDocuments> activityDocumentsList = activityDocumentRepository.findAllByActivity_IdActivity(activity.getIdActivity());
                    activityDocumentsList.forEach(
                            activityDocument -> idDocuments.add(activityDocument.getDocumentBranch().getIdDocumentation())
                    );
                }
        );

        if (newProviderSupplier == null) {
            newProviderSupplier = providerSupplierRepository.save(ProviderSupplier.builder()
                    .cnpj(contractProviderSupplierRequestDto.getProviderDatas().getCnpj())
                    .corporateName(contractProviderSupplierRequestDto.getProviderDatas().getCorporateName())
                    .email(contractProviderSupplierRequestDto.getProviderDatas().getEmail())
                    .telephone(contractProviderSupplierRequestDto.getProviderDatas().getTelephone())
                    .branches(List.of(branch))
                    .build());
        } else {
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

        if (JwtService.getAuthenticatedUserId() != null) {
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogContract(
                        savedContractProviderSupplier,
                        userResponsible.getEmail() + " created contract " + savedContractProviderSupplier.getContractReference(),
                        AuditLogContract.AuditLogContractActions.CREATE,
                        userResponsible);
            }
        }

        // salvar por tipagem de contrato / supplier
        documentBranch = documentBranchRepository.findAllById(idDocuments);

        ProviderSupplier finalNewProviderSupplier = newProviderSupplier;
        documentBranch.forEach(
                document -> documentProviderSupplier.add(DocumentProviderSupplier.builder()
                                .title(document.getTitle())
                                .status(Document.Status.PENDENTE)
                                .type(document.getType())
                                .isActive(true)
                                .documentMatrix(document.getDocumentMatrix())
                                .providerSupplier(finalNewProviderSupplier)
                                .build()));

        documentProviderSupplierRepository.saveAll(documentProviderSupplier);
        documentContractRepository.saveAll(documentContract);

        // criar solicitação
        crudItemManagementImpl.saveProviderSolicitation(ItemManagementProviderRequestDto.builder()
                        .solicitationType(ItemManagement.SolicitationType.CREATION)
                        .idRequester(requester.getIdUser())
                        .idNewProvider(newProviderSupplier.getIdProvider())
                .build());

        return ContractSupplierResponseDto.builder()
                .idContract(savedContractProviderSupplier.getIdContract())
                .serviceType(savedContractProviderSupplier.getServiceTypeBranch() != null ? savedContractProviderSupplier.getServiceTypeBranch().getIdServiceType() : null)
                .serviceDuration(savedContractProviderSupplier.getServiceDuration())
                .serviceName(savedContractProviderSupplier.getServiceName())
                .contractReference(savedContractProviderSupplier.getContractReference())
                .description(savedContractProviderSupplier.getDescription())
                .idResponsible(savedContractProviderSupplier.getResponsible() != null ? savedContractProviderSupplier.getResponsible().getIdUser() : null)
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .dateStart(savedContractProviderSupplier.getDateStart())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activities(savedContractProviderSupplier.getActivities()
                        .stream().map(Activity::getIdActivity).toList())
                .isActive(savedContractProviderSupplier.getIsActive())
                .idSupplier(savedContractProviderSupplier.getProviderSupplier() != null ? savedContractProviderSupplier.getProviderSupplier().getIdProvider() : null)
                .nameSupplier(savedContractProviderSupplier.getProviderSupplier() != null ? savedContractProviderSupplier.getProviderSupplier().getCorporateName() : null)
                .idBranch(savedContractProviderSupplier.getBranch() != null ? savedContractProviderSupplier.getBranch().getIdBranch() : null)
                .nameBranch(savedContractProviderSupplier.getBranch() != null ? savedContractProviderSupplier.getBranch().getName() : null)
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
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByIsActiveIsTrue(pageable);

        return contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceTypeBranch() != null ? contractProviderSupplier.getServiceTypeBranch().getIdServiceType() : null)
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .responsible(contractProviderSupplier.getResponsible() != null ? contractProviderSupplier.getResponsible().getIdUser() : null)
                        .expenseType(contractProviderSupplier.getExpenseType())
                        .dateStart(contractProviderSupplier.getDateStart())
                        .endDate(contractProviderSupplier.getEndDate())
                        .finished(contractProviderSupplier.getFinished())
                        .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
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
        ContractProviderSupplier contractProviderSupplier = contractProviderSupplierRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Supplier not found"));

        ServiceTypeBranch serviceTypeBranch = serviceTypeBranchRepository.findById(contractProviderSupplierRequestDto.getIdServiceType())
                .orElseThrow(() -> new NotFoundException("Service type not found"));

        UserClient userClient = userClientRepository.findById(contractProviderSupplierRequestDto.getResponsible())
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Activity> activities = activityRepository.findAllById(contractProviderSupplierRequestDto.getIdActivityList());

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
            User userResponsible = userRepository.findById(JwtService.getAuthenticatedUserId())
                    .orElse(null);
            if (userResponsible != null) {
                auditLogServiceImpl.createAuditLogContract(
                        savedContractProviderSupplier,
                        userResponsible.getEmail() + " updated contract " + savedContractProviderSupplier.getContractReference(),
                        AuditLogContract.AuditLogContractActions.UPDATE,
                        userResponsible);
            }
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
                .responsible(savedContractProviderSupplier.getResponsible() != null ? savedContractProviderSupplier.getResponsible().getIdUser() : null)
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .dateStart(savedContractProviderSupplier.getDateStart())
                .endDate(savedContractProviderSupplier.getEndDate())
                .finished(savedContractProviderSupplier.getFinished())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .activities(contractProviderSupplier.getActivities()
                        .stream().map(Activity::getIdActivity).toList())
                .providerSupplier(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getIdProvider() : null)
                .providerSupplierName(contractProviderSupplier.getProviderSupplier() != null ? contractProviderSupplier.getProviderSupplier().getCorporateName() : null)
                .branch(contractProviderSupplier.getBranch() != null ? contractProviderSupplier.getBranch().getIdBranch() : null)
                .branchName(contractProviderSupplier.getBranch() != null ? contractProviderSupplier.getBranch().getName() : null)
                .build();

        return Optional.of(contractResponseDto);
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
        contractProviderSupplierRepository.deleteById(id);
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplier(String idSearch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(idSearch, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @Override
    public Page<ContractResponseDto> findAllByClient(String idSearch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByBranch_IdBranchAndIsActiveIsTrue(idSearch, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @Override
    public Page<ContractResponseDto> findAllBySupplierAndBranch(String idSupplier, String idBranch, Pageable pageable) {
        Page<ContractProviderSupplier> contractProviderSupplierPage = contractProviderSupplierRepository.findAllByBranch_IdBranchAndProviderSupplier_IdProviderAndIsActiveIsTrue(idBranch,idSupplier, pageable);

        return getContractResponseDtos(contractProviderSupplierPage);
    }

    @NotNull
    private Page<ContractResponseDto> getContractResponseDtos(Page<ContractProviderSupplier> contractProviderSupplierPage) {

        return contractProviderSupplierPage.map(
                contractProviderSupplier -> ContractResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .serviceType(contractProviderSupplier.getServiceTypeBranch() != null ? contractProviderSupplier.getServiceTypeBranch().getIdServiceType() : null)
                        .serviceName(contractProviderSupplier.getServiceName())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .description(contractProviderSupplier.getDescription())
                        .expenseType(contractProviderSupplier.getExpenseType())
                        .dateStart(contractProviderSupplier.getDateStart())
                        .endDate(contractProviderSupplier.getEndDate())
                        .finished(contractProviderSupplier.getFinished())
                        .subcontractPermission(contractProviderSupplier.getSubcontractPermission())
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
        List<Activity> activities = List.of();
        List<Requirement> requirements = List.of();

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
                .responsible(savedContractProviderSupplier.getResponsible().getIdUser())
                .expenseType(savedContractProviderSupplier.getExpenseType())
                .startDate(savedContractProviderSupplier.getDateStart())
                .endDate(savedContractProviderSupplier.getEndDate())
                .subcontractPermission(savedContractProviderSupplier.getSubcontractPermission())
                .providerSupplierName(savedContractProviderSupplier.getProviderSupplier().getCorporateName())
                .idBranch(savedContractProviderSupplier.getBranch().getIdBranch())
                .branchName(savedContractProviderSupplier.getBranch().getName())
                .build();
    }

    @Override
    public List<ContractSupplierPermissionResponseDto> findAllByBranchAndSubcontractPermission(String idBranch) {
        List<ContractProviderSupplier> contractProviderSuppliers = contractProviderSupplierRepository.findAllByBranch_IdBranchAndSubcontractPermissionIsTrue(idBranch);
        return contractProviderSuppliers.stream().map(
                contractProviderSupplier -> ContractSupplierPermissionResponseDto.builder()
                        .idContract(contractProviderSupplier.getIdContract())
                        .contractReference(contractProviderSupplier.getContractReference())
                        .providerSupplierName(contractProviderSupplier.getProviderSupplier().getTradeName())
                        .build()
        )
                .sorted(Comparator.comparing(ContractSupplierPermissionResponseDto::getContractReference, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }
}
