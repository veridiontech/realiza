package bl.tech.realiza.gateways.repositories.employees;

import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeBrazilianRepository extends JpaRepository<EmployeeBrazilian, String> {
    Page<EmployeeBrazilian> findAllByClient_IdClient(String client, Pageable pageable);
    Page<EmployeeBrazilian> findAllBySupplier_IdProvider(String supplier, Pageable pageable);
    Page<EmployeeBrazilian> findAllBySubcontract_IdProvider(String subcontractor, Pageable pageable);
}
