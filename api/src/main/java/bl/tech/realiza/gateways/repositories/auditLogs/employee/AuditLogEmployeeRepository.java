package bl.tech.realiza.gateways.repositories.auditLogs.employee;

import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogEmployeeRepository extends JpaRepository<AuditLogEmployee, String> {
    Page<AuditLogEmployee> findAllByEmployee_idEmployee(String id, Pageable pageable);
    Page<AuditLogEmployee> findAllByEmployee_idEmployeeAndAction(String id, AuditLogActions action, Pageable pageable);
}
