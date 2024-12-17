package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeBrazilianRepository extends JpaRepository<EmployeeBrazilian, String> {
}
