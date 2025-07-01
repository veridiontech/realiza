package bl.tech.realiza.gateways.repositories.auditLogs.user;

import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogUserRepository extends JpaRepository<AuditLogUser, String> {
    Page<AuditLogUser> findAllByUserId(String id, Pageable pageable);
    Page<AuditLogUser> findAllByUserIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);
}
