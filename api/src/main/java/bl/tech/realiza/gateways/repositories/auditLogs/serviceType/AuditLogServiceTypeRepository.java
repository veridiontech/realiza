package bl.tech.realiza.gateways.repositories.auditLogs.serviceType;

import bl.tech.realiza.domains.auditLogs.serviceType.AuditLogServiceType;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogServiceTypeRepository extends JpaRepository<AuditLogServiceType, String> {
    Page<AuditLogServiceType> findAllByServiceType_idServiceType(String id, Pageable pageable);
    Page<AuditLogServiceType> findAllByServiceType_idServiceTypeAndAction(String id, AuditLogActions action, Pageable pageable);
}
