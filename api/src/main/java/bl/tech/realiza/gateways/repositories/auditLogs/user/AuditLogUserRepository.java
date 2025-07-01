package bl.tech.realiza.gateways.repositories.auditLogs.user;

import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogUserRepository extends JpaRepository<AuditLogUser, String> {
    Page<AuditLogUser> findAllByUser_idUser(String id, Pageable pageable);
    Page<AuditLogUser> findAllByUser_idUserAndAction(String id, AuditLogActions action, Pageable pageable);
}
