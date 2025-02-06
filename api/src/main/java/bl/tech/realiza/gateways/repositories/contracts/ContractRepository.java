package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ContractRepository extends JpaRepository<Contract, String> {
    Collection<Contract> findAllByIsActive(boolean b);
    Collection<Contract> findAllByDeleteRequest(boolean b);
}
