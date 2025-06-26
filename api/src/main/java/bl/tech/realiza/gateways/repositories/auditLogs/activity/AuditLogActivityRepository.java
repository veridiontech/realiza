package bl.tech.realiza.gateways.repositories.auditLogs.activity;

import bl.tech.realiza.domains.auditLogs.activity.AuditLogActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogActivityRepository extends JpaRepository<AuditLogActivity, String> {
}
