package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Collection<Employee> findAllByDeleteRequest(boolean b);
}
