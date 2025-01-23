package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, String> {
}
