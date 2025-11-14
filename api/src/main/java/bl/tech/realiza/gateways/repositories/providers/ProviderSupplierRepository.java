package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderSupplierRepository extends JpaRepository<ProviderSupplier, String>, JpaSpecificationExecutor<ProviderSupplier> {
    Page<ProviderSupplier> findAllByBranches_IdBranchAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<ProviderSupplier> findAllByBranches_IdBranchAndIsActiveIsTrue(String idSearch);
    Optional<ProviderSupplier> findByCnpj(String cnpj);
    Long countByBranches_IdBranchAndIsActiveTrue(String branchId);
    Page<ProviderSupplier> findAllByIsActiveIsTrue(Pageable pageable);

    @Query("""
    SELECT COUNT(ps)
    FROM ProviderSupplier ps
    JOIN ps.branches b
    WHERE b.client.idClient = :clientId AND ps.isActive = true
""")
    Long countByClientIdAndIsActive(@Param("clientId") String clientId);

    @Query("""
    SELECT ps
    FROM ProviderSupplier ps
    LEFT JOIN ps.contractsSupplier cs
    WHERE ps.isActive = true
    AND cs.status = :contractStatus
    AND cs.branch.client.idClient = :clientId
    AND ps.isActive = true
""")
    List<ProviderSupplier> findAllByClientIdAndContractStatusAndIsActiveIsTrue(
            @Param("clientId") String clientId,
            @Param("contractStatus") ContractStatusEnum contractStatus
    );

    @Query("""
    SELECT ps
    FROM ProviderSupplier ps
    LEFT JOIN ps.contractsSupplier cs
    WHERE ps.isActive = true
    AND cs.status = :contractStatus
    AND ps.isActive = true
    AND (:branchIds IS NULL OR cs.branch.idBranch IN :branchIds)
    AND (:responsibleIds IS NULL OR cs.responsible.idUser IN :responsibleIds)
""")
    List<ProviderSupplier> findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrue(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("contractStatus") ContractStatusEnum contractStatus
    );

    @Query("""
    SELECT COUNT(DISTINCT ps)
    FROM ProviderSupplier ps
    JOIN ps.contractsSupplier cs
    WHERE ps.isActive = true
    AND cs.status = 'ACTIVE'
    AND cs.finished = false
    AND cs.branch.idBranch = :branchId
""")
    Long countByActiveContractsInBranch(@Param("branchId") String branchId);
}
