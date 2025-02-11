package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    Collection<Employee> findAllByDeleteRequest(boolean b);
    Long countAllByBranch_IdBranch(String branchId);
    Long countAllBySupplier_IdProvider(String supplierId);
    Long countAllBySubcontract_IdProvider(String subcontractId);
}
