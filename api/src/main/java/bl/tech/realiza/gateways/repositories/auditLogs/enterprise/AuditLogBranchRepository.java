package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogBranchRepository extends JpaRepository<AuditLogBranch, String> {
    Page<AuditLogBranch> findAllByBranch_IdBranch(String id, Pageable pageable);
    Page<AuditLogBranch> findAllByBranch_IdBranchAndAction(String id, AuditLogActions action, Pageable pageable);
}
