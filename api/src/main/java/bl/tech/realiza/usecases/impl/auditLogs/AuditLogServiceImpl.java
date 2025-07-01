package bl.tech.realiza.usecases.impl.auditLogs;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.auditLogs.activity.AuditLogActivity;
import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import bl.tech.realiza.domains.auditLogs.serviceType.AuditLogServiceType;
import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogBoard;
import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogCenter;
import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogMarket;
import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.AuditLogActions;
import bl.tech.realiza.domains.enums.AuditLogType;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.ultragaz.Board;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.repositories.auditLogs.activity.AuditLogActivityRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.contract.AuditLogContractRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.document.AuditLogDocumentRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.employee.AuditLogEmployeeRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogBranchRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogClientRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogProviderRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.serviceType.AuditLogServiceTypeRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.ultragaz.AuditLogBoardRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.ultragaz.AuditLogCenterRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.ultragaz.AuditLogMarketRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.user.AuditLogUserRepository;
import bl.tech.realiza.gateways.responses.auditLog.AuditLogResponseDto;
import bl.tech.realiza.gateways.responses.clients.controlPanel.document.DocumentControlPanelResponseDto;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogContractRepository auditLogContractRepository;
    private final AuditLogEmployeeRepository auditLogEmployeeRepository;
    private final AuditLogBranchRepository auditLogBranchRepository;
    private final AuditLogClientRepository auditLogClientRepository;
    private final AuditLogProviderRepository auditLogProviderRepository;
    private final AuditLogBoardRepository auditLogBoardRepository;
    private final AuditLogCenterRepository auditLogCenterRepository;
    private final AuditLogMarketRepository auditLogMarketRepository;
    private final AuditLogUserRepository auditLogUserRepository;
    private final AuditLogDocumentRepository auditLogDocumentRepository;
    private final AuditLogServiceTypeRepository auditLogServiceTypeRepository;
    private final AuditLogActivityRepository auditLogActivityRepository;

    @Override
    public void createAuditLogContract(Contract contract, String description, AuditLogActions action, User userResponsible) {
        auditLogContractRepository.save(AuditLogContract.builder()
                .contract(contract)
                .action(action)
                .description(description)
                .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogEmployee(Employee employee, String description, AuditLogActions action, User userResponsible) {
        auditLogEmployeeRepository.save(AuditLogEmployee.builder()
                        .employee(employee)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogDocument(Document document, String description, AuditLogActions action, User userResponsible) {
        auditLogDocumentRepository.save(AuditLogDocument.builder()
                        .document(document)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogBranch(Branch branch, String description, AuditLogActions action, User userResponsible) {
        auditLogBranchRepository.save(AuditLogBranch.builder()
                        .branch(branch)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogActivity(Activity activity, String description, AuditLogActions action, User userResponsible) {
        auditLogActivityRepository.save(AuditLogActivity.builder()
                        .activity(activity)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogServiceType(ServiceType serviceType, String description, AuditLogActions action, User userResponsible) {
        auditLogServiceTypeRepository.save(AuditLogServiceType.builder()
                        .serviceType(serviceType)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogClient(Client client, String description, AuditLogActions action, User userResponsible) {
        auditLogClientRepository.save(AuditLogClient.builder()
                        .client(client)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogProvider(Provider provider, String description, AuditLogActions action, User userResponsible) {
        auditLogProviderRepository.save(AuditLogProvider.builder()
                        .provider(provider)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogBoard(Board board, String description, AuditLogActions action, User userResponsible) {
        auditLogBoardRepository.save(AuditLogBoard.builder()
                        .idBoard(board)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogCenter(Center center, String description, AuditLogActions action, User userResponsible) {
        auditLogCenterRepository.save(AuditLogCenter.builder()
                        .idCenter(center)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogMarket(Market market, String description, AuditLogActions action, User userResponsible) {
        auditLogMarketRepository.save(AuditLogMarket.builder()
                        .idMarket(market)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogUser(User user, String description, AuditLogActions action, User userResponsible) {
        auditLogUserRepository.save(AuditLogUser.builder()
                        .user(user)
                        .action(action)
                        .description(description)
                        .user(userResponsible)
                .build());
    }

    @Override
    public Page<AuditLogResponseDto> getAuditLogs(String id, AuditLogActions action, AuditLogType auditLogType, Pageable pageable) {
        switch (auditLogType) {
            case BRANCH -> {
                Page<AuditLogBranch> auditLogPage = action != null
                        ? auditLogBranchRepository.findAllByBranch_IdBranchAndAction(id, action, pageable)
                        : auditLogBranchRepository.findAllByBranch_IdBranch(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .branchId(auditLog.getBranch() != null
                                ? auditLog.getBranch().getIdBranch()
                                : null)
                        .branchName(auditLog.getBranch() != null
                                ? auditLog.getBranch().getName()
                                : null)
                        .build());
            }
            case ACTIVITY -> {
                Page<AuditLogActivity> auditLogPage = action != null
                        ? auditLogActivityRepository.findAllByActivity_idActivityAndAction(id, action, pageable)
                        : auditLogActivityRepository.findAllByActivity_idActivity(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .activityId(auditLog.getActivity() != null
                                ? auditLog.getActivity().getIdActivity()
                                : null)
                        .activityName(auditLog.getActivity() != null
                                ? auditLog.getActivity().getTitle()
                                : null)
                        .build());
            }
            case CLIENT -> {
                Page<AuditLogClient> auditLogPage = action != null
                        ? auditLogClientRepository.findAllByClient_idClientAndAction(id, action, pageable)
                        : auditLogClientRepository.findAllByClient_idClient(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .clientId(auditLog.getClient() != null
                                ? auditLog.getClient().getIdClient()
                                : null)
                        .clientName(auditLog.getClient() != null
                                ? auditLog.getClient().getCorporateName()
                                : null)
                        .build());
            }
            case EMPLOYEE -> {
                Page<AuditLogEmployee> auditLogPage = action != null
                        ? auditLogEmployeeRepository.findAllByEmployee_idEmployeeAndAction(id, action, pageable)
                        : auditLogEmployeeRepository.findAllByEmployee_idEmployee(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .employeeId(auditLog.getEmployee() != null
                                ? auditLog.getEmployee().getIdEmployee()
                                : null)
                        .employeeName(auditLog.getEmployee() != null
                                ? auditLog.getEmployee().getName()
                                    + (auditLog.getEmployee().getSurname() != null
                                    ? auditLog.getEmployee().getSurname()
                                    : "")
                                : null)
                        .build());
            }
            case DOCUMENT -> {
                Page<AuditLogDocument> auditLogPage = action != null
                        ? auditLogDocumentRepository.findAllByDocument_IdDocumentationAndAction(id, action, pageable)
                        : auditLogDocumentRepository.findAllByDocument_IdDocumentation(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .documentId(auditLog.getDocument() != null
                                ? auditLog.getDocument().getIdDocumentation()
                                : null)
                        .documentName(auditLog.getDocument() != null
                            ? auditLog.getDocument().getTitle()
                            : null)
                        .build());
            }
            case USER -> {
                Page<AuditLogUser> auditLogPage = action != null
                        ? auditLogUserRepository.findAllByUser_idUserAndAction(id, action, pageable)
                        : auditLogUserRepository.findAllByUser_idUser(id, pageable);
                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .userId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .userName(auditLog.getUser() != null
                            ? auditLog.getUser().getFirstName()
                            + (auditLog.getUser().getSurname() != null
                                ? auditLog.getUser().getSurname()
                                : "")
                            : null)
                        .build());
            }
            case CONTRACT -> {
                Page<AuditLogContract> auditLogPage = action != null
                        ? auditLogContractRepository.findAllByContract_idContractAndAction(id, action, pageable)
                        : auditLogContractRepository.findAllByContract_idContract(id, pageable);
                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .contractId(auditLog.getContract() != null
                                ? auditLog.getContract().getIdContract()
                                : null)
                        .contractName(auditLog.getContract() != null
                            ? auditLog.getContract().getContractReference()
                            : null)
                        .build());
            }
            case PROVIDER -> {
                Page<AuditLogProvider> auditLogPage = action != null
                        ? auditLogProviderRepository.findAllByProvider_idProviderAndAction(id, action, pageable)
                        : auditLogProviderRepository.findAllByProvider_idProvider(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .providerId(auditLog.getProvider() != null
                                ? auditLog.getProvider().getIdProvider()
                                : null)
                        .providerName(auditLog.getProvider() != null
                            ? auditLog.getProvider().getCorporateName()
                            : null)
                        .build());
            }
            case SERVICE_TYPE -> {
                Page<AuditLogServiceType> auditLogPage = action != null
                        ? auditLogServiceTypeRepository.findAllByServiceType_idServiceTypeAndAction(id, action, pageable)
                        : auditLogServiceTypeRepository.findAllByServiceType_idServiceType(id, pageable);

                return auditLogPage.map(auditLog -> AuditLogResponseDto.builder()
                        .id(auditLog.getIdRecord())
                        .description(auditLog.getDescription())
                        .notes(auditLog.getNotes())
                        .createdAt(auditLog.getCreatedAt())
                        .responsibleId(auditLog.getUser() != null
                                ? auditLog.getUser().getIdUser()
                                : null)
                        .responsibleFullName(auditLog.getUser() != null
                                ? (auditLog.getUser().getFirstName()
                                + (auditLog.getUser().getSurname() != null
                                    ? auditLog.getUser().getSurname()
                                    : ""))
                                : null)
                        .serviceTypeId(auditLog.getServiceType() != null
                                ? auditLog.getServiceType().getIdServiceType()
                                : null)
                        .serviceTypeName(auditLog.getServiceType() != null
                            ? auditLog.getServiceType().getTitle()
                            : null)
                        .build());
            }
            default -> throw new IllegalArgumentException("Invalid log type");
        }
    }
}
