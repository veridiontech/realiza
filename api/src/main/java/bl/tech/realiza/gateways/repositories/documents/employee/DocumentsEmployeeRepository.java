package bl.tech.realiza.gateways.repositories.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentsEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsEmployeeRepository extends JpaRepository<DocumentsEmployee, String> {
}
