package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogClientRepository extends JpaRepository<AuditLogClient, String> {
    Page<AuditLogClient> findAllByClient_idClient(String id, Pageable pageable);
    Page<AuditLogClient> findAllByClient_idClientAndAction(String id, AuditLogActions action, Pageable pageable);
}
