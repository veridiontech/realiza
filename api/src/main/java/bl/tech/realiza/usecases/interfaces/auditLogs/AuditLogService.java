package bl.tech.realiza.usecases.interfaces.auditLogs;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
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
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.ultragaz.Board;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.ultragaz.Market;
import bl.tech.realiza.domains.user.User;

public interface AuditLogService {
    Void createAuditLogContract(Contract contract, AuditLogContract.AuditLogContractActions action, User userResponsible);
    Void createAuditLogEmployee(Employee employee, AuditLogEmployee.AuditLogEmployeeActions action, User userResponsible);
    Void createAuditLogBranch(Branch branch, AuditLogBranch.AuditLogBranchActions action, User userResponsible);
    Void createAuditLogClient(Client client, AuditLogClient.AuditLogClientActions action, User userResponsible);
    Void createAuditLogProvider(Provider provider, AuditLogProvider.AuditLogProviderActions action, User userResponsible);
    Void createAuditLogBoard(Board board, AuditLogBoard.AuditLogBoardActions action, User userResponsible);
    Void createAuditLogCenter(Center center, AuditLogCenter.AuditLogCenterActions action, User userResponsible);
    Void createAuditLogMarket(Market market, AuditLogMarket.AuditLogMarketActions action, User userResponsible);
    Void createAuditLogUser(User user, AuditLogUser.AuditLogUserActions action, User userResponsible);
}
