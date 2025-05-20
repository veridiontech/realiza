package bl.tech.realiza.gateways.repositories.auditLogs.employee;

import bl.tech.realiza.domains.auditLogs.employee.AuditLogEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogEmployeeRepository extends JpaRepository<AuditLogEmployee, String> {
}
