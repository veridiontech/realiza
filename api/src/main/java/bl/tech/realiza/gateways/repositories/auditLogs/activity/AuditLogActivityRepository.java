package bl.tech.realiza.gateways.repositories.auditLogs.activity;

import bl.tech.realiza.domains.auditLogs.activity.AuditLogActivity;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogActivityRepository extends JpaRepository<AuditLogActivity, String> {
    Page<AuditLogActivity> findAllByActivity_idActivity(String id, Pageable pageable);
    Page<AuditLogActivity> findAllByActivity_idActivityAndAction(String id, AuditLogActions action, Pageable pageable);
}
