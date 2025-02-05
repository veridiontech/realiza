package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.EmployeeForeigner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeForeignerRepository extends JpaRepository<EmployeeForeigner, String> {
    Page<EmployeeForeigner> findAllByBranch_IdBranch(String client, Pageable pageable);
    Page<EmployeeForeigner> findAllBySupplier_IdProvider(String supplier, Pageable pageable);
    Page<EmployeeForeigner> findAllBySubcontract_IdProvider(String subcontract, Pageable pageable);
}
