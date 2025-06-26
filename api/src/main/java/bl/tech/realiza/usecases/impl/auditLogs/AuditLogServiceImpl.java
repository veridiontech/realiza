package bl.tech.realiza.usecases.impl.auditLogs;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogBoard;
import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogCenter;
import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogMarket;
import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.ultragaz.Board;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.gateways.repositories.auditLogs.contract.AuditLogContractRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.document.AuditLogDocumentRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.employee.AuditLogEmployeeRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogBranchRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogClientRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.enterprise.AuditLogProviderRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.ultragaz.AuditLogBoardRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.ultragaz.AuditLogCenterRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.ultragaz.AuditLogMarketRepository;
import bl.tech.realiza.gateways.repositories.auditLogs.user.AuditLogUserRepository;
import bl.tech.realiza.usecases.interfaces.auditLogs.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Override
    public void createAuditLogContract(Contract contract, String description, AuditLogContract.AuditLogContractActions action, User userResponsible) {
        auditLogContractRepository.save(AuditLogContract.builder()
                .contract(contract)
                .action(action)
                .description(description)
                .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogEmployee(Employee employee, String description, AuditLogEmployee.AuditLogEmployeeActions action, User userResponsible) {
        auditLogEmployeeRepository.save(AuditLogEmployee.builder()
                        .idEmployee(employee)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogDocument(Document document, String description, AuditLogDocument.AuditLogDocumentActions action, User userResponsible) {
        auditLogDocumentRepository.save(AuditLogDocument.builder()
                        .idDocumentation(document)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogBranch(Branch branch, String description, AuditLogBranch.AuditLogBranchActions action, User userResponsible) {
        auditLogBranchRepository.save(AuditLogBranch.builder()
                        .idBranch(branch)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogClient(Client client, String description, AuditLogClient.AuditLogClientActions action, User userResponsible) {
        auditLogClientRepository.save(AuditLogClient.builder()
                        .idClient(client)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogProvider(Provider provider, String description, AuditLogProvider.AuditLogProviderActions action, User userResponsible) {
        auditLogProviderRepository.save(AuditLogProvider.builder()
                        .idProvider(provider)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogBoard(Board board, String description, AuditLogBoard.AuditLogBoardActions action, User userResponsible) {
        auditLogBoardRepository.save(AuditLogBoard.builder()
                        .idBoard(board)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogCenter(Center center, String description, AuditLogCenter.AuditLogCenterActions action, User userResponsible) {
        auditLogCenterRepository.save(AuditLogCenter.builder()
                        .idCenter(center)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogMarket(Market market, String description, AuditLogMarket.AuditLogMarketActions action, User userResponsible) {
        auditLogMarketRepository.save(AuditLogMarket.builder()
                        .idMarket(market)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }

    @Override
    public void createAuditLogUser(User user, String description, AuditLogUser.AuditLogUserActions action, User userResponsible) {
        auditLogUserRepository.save(AuditLogUser.builder()
                        .idUser(user)
                        .action(action)
                        .description(description)
                        .idUser(userResponsible)
                .build());
    }
}
