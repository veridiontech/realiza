package bl.tech.realiza.gateways.repositories.auditLogs.serviceType;

import bl.tech.realiza.domains.auditLogs.serviceType.AuditLogServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogServiceTypeRepository extends JpaRepository<AuditLogServiceType, String> {
}
