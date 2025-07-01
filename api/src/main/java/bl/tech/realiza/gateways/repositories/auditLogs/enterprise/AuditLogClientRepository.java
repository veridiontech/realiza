package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogClientRepository extends JpaRepository<AuditLogClient, String> {
    Page<AuditLogClient> findAllByClientId(String id, Pageable pageable);
    Page<AuditLogClient> findAllByClientIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);
}
