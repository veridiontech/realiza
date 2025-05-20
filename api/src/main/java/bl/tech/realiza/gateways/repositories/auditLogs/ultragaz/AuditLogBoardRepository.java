package bl.tech.realiza.gateways.repositories.auditLogs.ultragaz;

import bl.tech.realiza.domains.auditLogs.ultragaz.AuditLogBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogBoardRepository extends JpaRepository<AuditLogBoard, String> {
}
