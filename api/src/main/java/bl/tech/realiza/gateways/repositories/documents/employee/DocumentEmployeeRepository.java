package bl.tech.realiza.gateways.repositories.documents.employee;

import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEmployeeRepository extends JpaRepository<DocumentEmployee, String> {
    Page<DocumentEmployee> findAllByEmployee_IdEmployee(String idSearch, Pageable pageable);
}
