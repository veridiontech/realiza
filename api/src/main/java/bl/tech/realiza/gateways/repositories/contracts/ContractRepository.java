package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ContractRepository extends JpaRepository<Contract, String> {
    Collection<Contract> findAllByDeleteRequest(boolean b);
    Page<Contract> findAllByEmployees_IdEmployee(String idEmployee, Pageable pageable);
}
