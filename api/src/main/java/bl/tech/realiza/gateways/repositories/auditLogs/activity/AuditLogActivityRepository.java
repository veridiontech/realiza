package bl.tech.realiza.gateways.repositories.auditLogs.activity;

import bl.tech.realiza.domains.auditLogs.activity.AuditLogActivity;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogActivityRepository extends JpaRepository<AuditLogActivity, String> {
    Page<AuditLogActivity> findAllByActivityId(String id, Pageable pageable);
    Page<AuditLogActivity> findAllByActivityIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);

    Page<AuditLogActivity> findAllByActivityIdAndUserResponsibleId(String id, String idUser, Pageable pageable);

    Page<AuditLogActivity> findAllByActivityIdAndActionAndUserResponsibleId(String id, AuditLogActionsEnum action, String idUser, Pageable pageable);
}
