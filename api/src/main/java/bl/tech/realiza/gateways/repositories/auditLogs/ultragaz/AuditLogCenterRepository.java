package bl.tech.realiza.gateways.repositories.auditLogs.ultragaz;

import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogCenter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogCenterRepository extends JpaRepository<AuditLogCenter, String> {
}
