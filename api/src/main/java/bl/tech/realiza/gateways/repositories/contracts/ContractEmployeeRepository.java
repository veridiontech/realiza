package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.contract.ContractEmployee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractEmployeeRepository extends JpaRepository<ContractEmployee, String> {
    Optional<ContractEmployee> findByContract_IdContractAndEmployee_IdEmployee(String contractId, String employeeId);
}
