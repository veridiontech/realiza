package bl.tech.realiza.gateways.repositories.clients;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, String> {
    Page<Branch> findAllByClient_IdClientAndIsActiveIsTrue(String client, Pageable pageable);
    Page<Branch> findAllByIsActiveIsTrue(Pageable pageable);
    Optional<Branch> findByCnpj(String cnpj);
    Collection<Branch> findAllByIsActive(boolean b);
}
