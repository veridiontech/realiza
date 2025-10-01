package bl.tech.realiza.usecases.impl.auditLogs;

import bl.tech.realiza.domains.auditLogs.activity.AuditLogActivity;
import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import bl.tech.realiza.domains.auditLogs.serviceType.AuditLogServiceType;
import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import bl.tech.realiza.domains.enums.AuditLogTypeEnum;
import bl.tech.realiza.domains.enums.OwnerEnum;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.services.FileDocument;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.exceptions.NotFoundException;
import bl.tech.realiza.gateways.repositories.auditLogs.activity.AuditLogActivityRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.contract.AuditLogContractRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.document.AuditLogDocumentRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.employee.AuditLogEmployeeRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogBranchRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogClientRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogProviderRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.serviceType.AuditLogServiceTypeRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.user.AuditLogUserRepository;
import bl.tech.realiza.gateways.repositories.clients.BranchRepository;
import bl.tech.realiza.gateways.repositories.clients.ClientRepository;
import bl.tech.realiza.gateways.repositories.contracts.ContractRepository;
import bl.tech.realiza.gateways.repositories.contracts.activity.ActivityRepository;
import bl.tech.realiza.gateways.repositories.contracts.serviceType.ServiceTypeBranchRepository;
import bl.tech.realiza.gateways.repositories.documents.DocumentRepository;
import bl.tech.realiza.gateways.repositories.employees.EmployeeRepository;
import bl.tech.realiza.gateways.repositories.providers.ProviderRepository;
import bl.tech.realiza.gateways.repositories.users.UserRepository;
import bl.tech.realiza.gateways.responses.auditLog.AuditLogResponseDto;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;

import static bl.tech.realiza.domains.enums.AuditLogActionsEnum.*;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogContractRepository auditLogContractRepository;
    private final AuditLogEmployeeRepository auditLogEmployeeRepository;
    private final AuditLogBranchRepository auditLogBranchRepository;
    private final AuditLogClientRepository auditLogClientRepository;
    private final AuditLogProviderRepository auditLogProviderRepository;
    private final AuditLogUserRepository auditLogUserRepository;
    private final AuditLogDocumentRepository auditLogDocumentRepository;
    private final AuditLogServiceTypeRepository auditLogServiceTypeRepository;
    private final AuditLogActivityRepository auditLogActivityRepository;
    private final UserRepository userRepository;
    private final ServiceTypeBranchRepository serviceTypeBranchRepository;
    private final ProviderRepository providerRepository;
    private final ContractRepository contractRepository;
    private final DocumentRepository documentRepository;
    private final EmployeeRepository employeeRepository;
    private final ClientRepository clientRepository;
    private final BranchRepository branchRepository;
    private final ActivityRepository activityRepository;

    @Override
    public void createAuditLog(String id,
                               AuditLogTypeEnum typeEnum,
                               String description,
                               String justification,
                               String notes,
                               AuditLogActionsEnum action,
                               String userResponsibleId) {
        User userResponsible = userRepository.findById(userResponsibleId)
                .orElseThrow(() ->  new NotFoundException("User not found"));
        switch (typeEnum) {
            case SERVICE_TYPE -> {
                ServiceTypeBranch serviceTypeBranch = serviceTypeBranchRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Service type not found"));
                auditLogServiceTypeRepository.save(
                        AuditLogServiceType.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .serviceTypeId(serviceTypeBranch.getIdServiceType())
                                .serviceTypeTitle(serviceTypeBranch.getTitle())
                                .build()
                );
            }
            case PROVIDER -> {
                Provider provider = providerRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Provider not found"));
                auditLogProviderRepository.save(
                        AuditLogProvider.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .providerId(provider.getIdProvider())
                                .providerCorporateName(provider.getCorporateName())
                                .build()
                );
            }
            case CONTRACT -> {
                Contract contract = contractRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Contract not found"));
                if (contract instanceof ContractProviderSupplier contractSupplier) {
                    auditLogContractRepository.save(
                            AuditLogContract.builder()
                                    .description(description)
                                    .justification(justification)
                                    .notes(notes)
                                    .action(action)
                                    .userResponsibleId(userResponsible.getIdUser())
                                    .userResponsibleEmail(userResponsible.getEmail())
                                    .userResponsibleCpf(userResponsible.getCpf())
                                    .userResponsibleFullName(userResponsible.getFullName())
                                    .branchId(contractSupplier.getBranch() != null
                                            ? contractSupplier.getBranch().getIdBranch()
                                            : null)
                                    .branchName(contractSupplier.getBranch() != null
                                            ? contractSupplier.getBranch().getName()
                                            : null)
                                    .supplierId(contractSupplier.getProviderSupplier() != null
                                            ? contractSupplier.getProviderSupplier().getIdProvider()
                                            : null)
                                    .supplierCorporateName(contractSupplier.getProviderSupplier() != null
                                            ? contractSupplier.getProviderSupplier().getCorporateName()
                                            : null)
                                    .contractId(contract.getIdContract())
                                    .contractReference(contract.getContractReference())
                                    .responsibleId(contract.getResponsible() != null
                                            ? contract.getResponsible().getIdUser()
                                            : null)
                                    .responsibleFullName(contract.getResponsible() != null
                                            ? contract.getResponsible().getFullName()
                                            : null)
                                    .clientId(contractSupplier.getBranch() != null
                                            ? (contractSupplier.getBranch().getClient() != null
                                                ? contractSupplier.getBranch().getClient().getIdClient()
                                                : null)
                                            : null)
                                    .clientCorporateName(contractSupplier.getBranch() != null
                                            ? (contractSupplier.getBranch().getClient() != null
                                            ? contractSupplier.getBranch().getClient().getCorporateName()
                                            : null)
                                            : null)
                                    .build()
                    );
                } else if (contract instanceof ContractProviderSubcontractor contractSubcontractor) {
                    auditLogContractRepository.save(
                            AuditLogContract.builder()
                                    .description(description)
                                    .justification(justification)
                                    .notes(notes)
                                    .action(action)
                                    .userResponsibleId(userResponsible.getIdUser())
                                    .userResponsibleEmail(userResponsible.getEmail())
                                    .userResponsibleCpf(userResponsible.getCpf())
                                    .userResponsibleFullName(userResponsible.getFullName())
                                    .supplierId(contractSubcontractor.getProviderSupplier() != null
                                            ? contractSubcontractor.getProviderSupplier().getIdProvider()
                                            : null)
                                    .supplierCorporateName(contractSubcontractor.getProviderSupplier() != null
                                            ? contractSubcontractor.getProviderSupplier().getCorporateName()
                                            : null)
                                    .subcontractorId(contractSubcontractor.getProviderSubcontractor() != null
                                            ? contractSubcontractor.getProviderSubcontractor().getIdProvider()
                                            : null)
                                    .subcontractorCorporateName(contractSubcontractor.getProviderSubcontractor() != null
                                            ? contractSubcontractor.getProviderSubcontractor().getCorporateName()
                                            : null)
                                    .build()
                    );
                }
            }
            case USER -> {
                User user = userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("User not found"));
                auditLogUserRepository.save(
                        AuditLogUser.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .userId(user.getIdUser())
                                .userEmail(user.getEmail())
                                .userCpf(user.getCpf())
                                .userFullName(user.getFullName())
                                .build()
                );
            }
            case DOCUMENT -> {
                Document document = documentRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Document not found"));
                if (document instanceof DocumentProviderSupplier documentSupplier) {
                    auditLogDocumentRepository.save(
                            AuditLogDocument.builder()
                                    .description(description)
                                    .justification(justification)
                                    .notes(notes)
                                    .action(action)
                                    .userResponsibleId(userResponsible.getIdUser())
                                    .userResponsibleEmail(userResponsible.getEmail())
                                    .userResponsibleCpf(userResponsible.getCpf())
                                    .userResponsibleFullName(userResponsible.getFullName())
                                    .ownerId(documentSupplier.getProviderSupplier() != null
                                            ? documentSupplier.getProviderSupplier().getIdProvider()
                                            : null)
                                    .owner(OwnerEnum.SUPPLIER)
                                    .documentId(documentSupplier.getIdDocumentation())
                                    .documentTitle(documentSupplier.getTitle())
                                    .fileId(!documentSupplier.getDocument().isEmpty()
                                            ? documentSupplier.getDocument().stream().max(Comparator.comparing(FileDocument::getCreationDate)).get().getId()
                                            : null)
                                    .hasDoc(true)
                                    .build()
                    );
                } else if (document instanceof DocumentProviderSubcontractor documentSubcontractor) {
                    auditLogDocumentRepository.save(
                            AuditLogDocument.builder()
                                    .description(description)
                                    .justification(justification)
                                    .notes(notes)
                                    .action(action)
                                    .userResponsibleId(userResponsible.getIdUser())
                                    .userResponsibleEmail(userResponsible.getEmail())
                                    .userResponsibleCpf(userResponsible.getCpf())
                                    .userResponsibleFullName(userResponsible.getFullName())
                                    .ownerId(documentSubcontractor.getProviderSubcontractor() != null
                                            ? documentSubcontractor.getProviderSubcontractor().getIdProvider()
                                            : null)
                                    .owner(OwnerEnum.SUBCONTRACTOR)
                                    .documentId(documentSubcontractor.getIdDocumentation())
                                    .documentTitle(documentSubcontractor.getTitle())
                                    .fileId(!documentSubcontractor.getDocument().isEmpty()
                                            ? documentSubcontractor.getDocument().stream().max(Comparator.comparing(FileDocument::getCreationDate)).get().getId()
                                            : null)
                                    .hasDoc(true)
                                    .build()
                    );
                } else if (document instanceof DocumentEmployee documentEmployee) {
                    auditLogDocumentRepository.save(
                            AuditLogDocument.builder()
                                    .description(description)
                                    .justification(justification)
                                    .notes(notes)
                                    .action(action)
                                    .userResponsibleId(userResponsible.getIdUser())
                                    .userResponsibleEmail(userResponsible.getEmail())
                                    .userResponsibleCpf(userResponsible.getCpf())
                                    .userResponsibleFullName(userResponsible.getFullName())
                                    .ownerId(documentEmployee.getEmployee() != null
                                            ? documentEmployee.getEmployee().getIdEmployee()
                                            : null)
                                    .owner(OwnerEnum.EMPLOYEE)
                                    .documentId(documentEmployee.getIdDocumentation())
                                    .documentTitle(documentEmployee.getTitle())
                                    .fileId(!documentEmployee.getDocument().isEmpty()
                                            ? documentEmployee.getDocument().stream().max(Comparator.comparing(FileDocument::getCreationDate)).get().getId()
                                            : null)
                                    .hasDoc(true)
                                    .build()
                    );
                }
            }
            case EMPLOYEE -> {
                Employee employee = employeeRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Employee not found"));
                auditLogEmployeeRepository.save(
                        AuditLogEmployee.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .employeeId(employee.getIdEmployee())
                                .employeeFullName(employee.getFullName())
                                .enterpriseId(employee.getSupplier() != null
                                        ? employee.getSupplier().getIdProvider()
                                        : employee.getSubcontract() != null
                                            ? employee.getSubcontract().getIdProvider()
                                            : null)
                                .enterpriseCorporateName(employee.getSupplier() != null
                                        ? employee.getSupplier().getCorporateName()
                                        : employee.getSubcontract() != null
                                        ? employee.getSubcontract().getCorporateName()
                                        : null)
                                .build()
                );
            }
            case CLIENT -> {
                Client client = clientRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Client not found"));
                auditLogClientRepository.save(
                        AuditLogClient.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .clientId(client.getIdClient())
                                .clientCorporateName(client.getCorporateName())
                                .build()
                );
            }
            case BRANCH -> {
                Branch branch = branchRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Branch not found"));
                auditLogBranchRepository.save(
                        AuditLogBranch.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .branchId(branch.getIdBranch())
                                .branchName(branch.getName())
                                .build()
                );
            }
            case ACTIVITY -> {
                Activity activity = activityRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("Activity not found"));
                auditLogActivityRepository.save(
                        AuditLogActivity.builder()
                                .description(description)
                                .justification(justification)
                                .notes(notes)
                                .action(action)
                                .userResponsibleId(userResponsible.getIdUser())
                                .userResponsibleEmail(userResponsible.getEmail())
                                .userResponsibleCpf(userResponsible.getCpf())
                                .userResponsibleFullName(userResponsible.getFullName())
                                .activityId(activity.getIdActivity())
                                .activityTitle(activity.getTitle())
                                .branchId(activity.getBranch() != null
                                        ? activity.getBranch().getIdBranch()
                                        : null)
                                .branchName(activity.getBranch() != null
                                        ? activity.getBranch().getName()
                                        : null)
                                .build()
                );
            }
        }
    }

    @Override
    public Page<AuditLogResponseDto> getAuditLogs(String id, AuditLogActionsEnum action, AuditLogTypeEnum auditLogTypeEnum, String userResponsibleId, Pageable pageable) {
        User user = null;
        if (userResponsibleId != null && !userResponsibleId.isEmpty()) {
            user = userRepository.findById(userResponsibleId)
                    .orElseThrow(() -> new NotFoundException("User not found"));
        }
        switch (auditLogTypeEnum) {
            case BRANCH -> {
                Page<AuditLogBranch> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogBranchRepository.findAllByBranchIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogBranchRepository.findAllByBranchId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogBranchRepository.findAllByBranchIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogBranchRepository.findAllByBranchIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .branchId(auditLog.getBranchId())
                        .branchName(auditLog.getBranchName())
                        .build());
            }
            case ACTIVITY -> {
                Page<AuditLogActivity> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogActivityRepository.findAllByActivityIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogActivityRepository.findAllByActivityId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogActivityRepository.findAllByActivityIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogActivityRepository.findAllByActivityIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .activityId(auditLog.getActivityId())
                        .activityTitle(auditLog.getActivityTitle())
                        .branchId(auditLog.getBranchId())
                        .branchName(auditLog.getBranchName())
                        .build());
            }
            case CLIENT -> {
                Page<AuditLogClient> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogClientRepository.findAllByClientIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogClientRepository.findAllByClientId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogClientRepository.findAllByClientIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogClientRepository.findAllByClientIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .clientId(auditLog.getClientId())
                        .clientCorporateName(auditLog.getClientCorporateName())
                        .build());
            }
            case EMPLOYEE -> {
                Page<AuditLogEmployee> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogEmployeeRepository.findAllByEmployeeIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogEmployeeRepository.findAllByEmployeeId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogEmployeeRepository.findAllByEmployeeIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogEmployeeRepository.findAllByEmployeeIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .employeeId(auditLog.getEmployeeId())
                        .employeeName(auditLog.getEmployeeFullName())
                        .enterpriseId(auditLog.getEnterpriseId())
                        .enterpriseCorporateName(auditLog.getEnterpriseCorporateName())
                        .build());
            }
            case DOCUMENT -> {
                Page<AuditLogDocument> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogDocumentRepository.findAllByDocumentIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogDocumentRepository.findAllByDocumentId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogDocumentRepository.findAllByDocumentIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogDocumentRepository.findAllByDocumentIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .documentId(auditLog.getDocumentId())
                        .documentTitle(auditLog.getDocumentTitle())
                        .owner(auditLog.getOwner())
                        .ownerId(auditLog.getOwnerId())
                        .fileId(auditLog.getFileId())
                        .hasDoc(auditLog.getHasDoc())
                        .build());
            }
            case USER -> {
                Page<AuditLogUser> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogUserRepository.findAllByUserIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogUserRepository.findAllByUserId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogUserRepository.findAllByUserIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogUserRepository.findAllByUserIdAndAction(id, action, pageable);
                    }
                }
                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .userId(auditLog.getUserId())
                        .userCpf(auditLog.getUserCpf())
                        .userFullName(auditLog.getUserFullName())
                        .userEmail(auditLog.getUserEmail())
                        .build());
            }
            case CONTRACT -> {
                Page<AuditLogContract> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogContractRepository.findAllByContractIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogContractRepository.findAllByContractId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogContractRepository.findAllByContractIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogContractRepository.findAllByContractIdAndAction(id, action, pageable);
                    }
                }
                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .contractId(auditLog.getContractId())
                        .contractReference(auditLog.getContractReference())
                        .responsibleId(auditLog.getResponsibleId())
                        .responsibleFullName(auditLog.getResponsibleFullName())
                        .clientId(auditLog.getClientId())
                        .clientCorporateName(auditLog.getClientCorporateName())
                        .branchId(auditLog.getBranchId())
                        .branchName(auditLog.getBranchName())
                        .supplierId(auditLog.getSupplierId())
                        .supplierCorporateName(auditLog.getSupplierCorporateName())
                        .subcontractorId(auditLog.getSubcontractorId())
                        .subcontractorCorporateName(auditLog.getSubcontractorCorporateName())
                        .build());
            }
            case PROVIDER -> {
                Page<AuditLogProvider> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogProviderRepository.findAllByProviderIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogProviderRepository.findAllByProviderId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogProviderRepository.findAllByProviderIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogProviderRepository.findAllByProviderIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .providerId(auditLog.getProviderId())
                        .providerCorporateName(auditLog.getProviderCorporateName())
                        .build());
            }
            case SERVICE_TYPE -> {
                Page<AuditLogServiceType> auditLogPage = null;
                if (action == ALL || action == null) {
                    if (user != null) {
                        auditLogPage = auditLogServiceTypeRepository.findAllByServiceTypeIdAndUserResponsibleId(id, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogServiceTypeRepository.findAllByServiceTypeId(id, pageable);
                    }
                } else {
                    if (user != null) {
                        auditLogPage = auditLogServiceTypeRepository.findAllByServiceTypeIdAndActionAndUserResponsibleId(id, action, user.getIdUser(), pageable);
                    } else {
                        auditLogPage = auditLogServiceTypeRepository.findAllByServiceTypeIdAndAction(id, action, pageable);
                    }
                }

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .justification(auditLog.getJustification())
                        .action(auditLog.getAction())
                        .createdAt(auditLog.getCreatedAt())
                        .date(auditLog.getCreatedAt())
                        .userResponsibleId(auditLog.getUserResponsibleId())
                        .userResponsibleCpf(auditLog.getUserResponsibleCpf())
                        .userResponsibleFullName(auditLog.getUserResponsibleFullName())
                        .userResponsibleEmail(auditLog.getUserResponsibleEmail())
                        .serviceTypeId(auditLog.getServiceTypeId())
                        .serviceTypeTitle(auditLog.getServiceTypeTitle())
                        .branchId(auditLog.getBranchId())
                        .branchName(auditLog.getBranchName())
                        .build());
            }
            default -> throw new IllegalArgumentException("Invalid log type");
        }
    }
}
