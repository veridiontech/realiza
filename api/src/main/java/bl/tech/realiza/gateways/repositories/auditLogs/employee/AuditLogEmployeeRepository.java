package bl.tech.realiza.gateways.repositories.auditLogs.employee;

import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogEmployeeRepository extends JpaRepository<AuditLogEmployee, String> {
    Page<AuditLogEmployee> findAllByEmployeeId(String id, Pageable pageable);
    Page<AuditLogEmployee> findAllByEmployeeIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);

    Page<AuditLogEmployee> findAllByEmployeeIdAndUserResponsibleId(String id, String idUser, Pageable pageable);

    Page<AuditLogEmployee> findAllByEmployeeIdAndActionAndUserResponsibleId(String id, AuditLogActionsEnum action, String idUser, Pageable pageable);
}
