package bl.tech.realiza.gateways.repositories.documents.employee;

import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEmployeeRepository extends JpaRepository<DocumentEmployee, String> {
}
