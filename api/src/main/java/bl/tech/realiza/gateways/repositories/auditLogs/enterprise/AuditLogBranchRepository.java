package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogBranchRepository extends JpaRepository<AuditLogBranch, String> {
}
