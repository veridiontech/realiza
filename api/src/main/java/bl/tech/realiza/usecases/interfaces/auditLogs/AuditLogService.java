package bl.tech.realiza.usecases.interfaces.auditLogs;

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
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.ultragaz.Board;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.domains.user.User;
import org.springframework.scheduling.annotation.Async;

public interface AuditLogService {
    @Async
    void createAuditLogContract(Contract contract,String description, AuditLogContract.AuditLogContractActions action, User userResponsible);
    @Async
    void createAuditLogEmployee(Employee employee,String description, AuditLogEmployee.AuditLogEmployeeActions action, User userResponsible);
    @Async
    void createAuditLogDocument(Document document, String description, AuditLogDocument.AuditLogDocumentActions action, User userResponsible);
    @Async
    void createAuditLogBranch(Branch branch,String description, AuditLogBranch.AuditLogBranchActions action, User userResponsible);
    @Async
    void createAuditLogActivity(Activity activity, String description, AuditLogActivity.AuditLogActivityActions action, User userResponsible);
    @Async
    void createAuditLogServiceType(ServiceType serviceType, String description, AuditLogServiceType.AuditLogServiceTypeActions action, User userResponsible);
    @Async
    void createAuditLogClient(Client client,String description, AuditLogClient.AuditLogClientActions action, User userResponsible);
    @Async
    void createAuditLogProvider(Provider provider,String description, AuditLogProvider.AuditLogProviderActions action, User userResponsible);
    @Async
    void createAuditLogBoard(Board board,String description, AuditLogBoard.AuditLogBoardActions action, User userResponsible);
    @Async
    void createAuditLogCenter(Center center,String description, AuditLogCenter.AuditLogCenterActions action, User userResponsible);
    @Async
    void createAuditLogMarket(Market market,String description, AuditLogMarket.AuditLogMarketActions action, User userResponsible);
    @Async
    void createAuditLogUser(User user,String description, AuditLogUser.AuditLogUserActions action, User userResponsible);
}
