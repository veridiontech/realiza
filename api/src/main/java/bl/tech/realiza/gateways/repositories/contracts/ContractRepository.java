package bl.tech.realiza.gateways.repositories.contracts;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.gateways.responses.contracts.contract.ContractByBranchIdsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, String> {
    Collection<Contract> findAllByDeleteRequest(boolean b);
    Page<Contract> findAllByEmployees_IdEmployee(String idEmployee, Pageable pageable);

    @Query("""
    SELECT new bl.tech.realiza.gateways.responses.contracts.contract.ContractByBranchIdsResponseDto(
        c.idContract,
        c.contractReference,
        cps.branch.idBranch,
        cps.branch.name
    )
    FROM Contract c
    LEFT JOIN ContractProviderSupplier cps ON c.idContract = cps.idContract
    WHERE cps.branch.idBranch IN :branchIds
""")
    List<ContractByBranchIdsResponseDto> findAllByBranchIds(@Param("branchIds") List<String> branchIds);
}
