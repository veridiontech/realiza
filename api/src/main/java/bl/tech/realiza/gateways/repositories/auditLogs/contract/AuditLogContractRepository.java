package bl.tech.realiza.gateways.repositories.auditLogs.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogContractRepository extends JpaRepository<AuditLogContract, String> {
}
