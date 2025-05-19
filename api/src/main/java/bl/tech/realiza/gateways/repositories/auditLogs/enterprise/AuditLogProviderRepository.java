package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogProviderRepository extends JpaRepository<AuditLogProvider, String> {
}
