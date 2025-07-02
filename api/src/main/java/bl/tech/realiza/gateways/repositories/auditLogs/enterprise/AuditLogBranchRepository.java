package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogBranchRepository extends JpaRepository<AuditLogBranch, String> {
    Page<AuditLogBranch> findAllByBranchId(String id, Pageable pageable);
    Page<AuditLogBranch> findAllByBranchIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);

    Page<AuditLogBranch> findAllByBranchIdAndUserResponsibleId(String id, String idUser, Pageable pageable);

    Page<AuditLogBranch> findAllByBranchIdAndActionAndUserResponsibleId(String id, AuditLogActionsEnum action, String idUser, Pageable pageable);
}
