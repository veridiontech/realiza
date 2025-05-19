package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogClientRepository extends JpaRepository<AuditLogClient, String> {
}
