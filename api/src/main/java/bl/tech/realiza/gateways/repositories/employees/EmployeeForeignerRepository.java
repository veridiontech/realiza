package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.EmployeeForeigner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeForeignerRepository extends JpaRepository<EmployeeForeigner, String> {
}
