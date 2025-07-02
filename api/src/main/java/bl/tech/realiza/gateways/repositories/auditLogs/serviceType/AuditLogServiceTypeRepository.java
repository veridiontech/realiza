package bl.tech.realiza.gateways.repositories.auditLogs.serviceType;

import bl.tech.realiza.domains.auditLogs.serviceType.AuditLogServiceType;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogServiceTypeRepository extends JpaRepository<AuditLogServiceType, String> {
    Page<AuditLogServiceType> findAllByServiceTypeId(String id, Pageable pageable);
    Page<AuditLogServiceType> findAllByServiceTypeIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);

    Page<AuditLogServiceType> findAllByServiceTypeIdAndUserResponsibleId(String id, String idUser, Pageable pageable);

    Page<AuditLogServiceType> findAllByServiceTypeIdAndActionAndUserResponsibleId(String id, AuditLogActionsEnum action, String idUser, Pageable pageable);
}
