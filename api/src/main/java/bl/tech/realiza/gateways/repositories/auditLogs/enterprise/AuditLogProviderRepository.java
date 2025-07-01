package bl.tech.realiza.gateways.repositories.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogProviderRepository extends JpaRepository<AuditLogProvider, String> {
    Page<AuditLogProvider> findAllByProvider_idProvider(String id, Pageable pageable);
    Page<AuditLogProvider> findAllByProvider_idProviderAndAction(String id, AuditLogActions action, Pageable pageable);
}
