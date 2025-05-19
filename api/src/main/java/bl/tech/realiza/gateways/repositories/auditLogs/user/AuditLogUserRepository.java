package bl.tech.realiza.gateways.repositories.auditLogs.user;

import bl.tech.realiza.domains.auditLogs.user.AuditLogUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogUserRepository extends JpaRepository<AuditLogUser, String> {
}
