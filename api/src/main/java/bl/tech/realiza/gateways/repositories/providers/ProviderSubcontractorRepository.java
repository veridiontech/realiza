package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderSubcontractorRepository extends JpaRepository<ProviderSubcontractor, String>, JpaSpecificationExecutor<ProviderSubcontractor> {
    Optional<ProviderSubcontractor> findByCnpj(String cnpj);
    List<ProviderSubcontractor> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch);
    Long countByProviderSupplier_IdProviderInAndIsActiveIsTrue(List<String> idSearch);
    Long countByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch);
    Page<ProviderSubcontractor> findAllByIsActiveIsTrue(Pageable pageable);
    Page<ProviderSubcontractor> findAllByProviderSupplier_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);

    @Query("""
    SELECT ps
    FROM ProviderSubcontractor ps
    LEFT JOIN ps.contractsSubcontractor cs
    WHERE ps.isActive = true
    AND cs.status = :contractStatus
    AND cs.contractProviderSupplier.branch.client.idClient = :clientId
    AND ps.isActive = true
""")
    List<ProviderSubcontractor> findAllByContractSupplierClientIdAndContractStatusAndIsActiveIsTrue(
            @Param("clientId") String clientId,
            @Param("contractStatus") ContractStatusEnum contractStatus
    );

    @Query("""
    SELECT ps
    FROM ProviderSubcontractor ps
    LEFT JOIN ps.contractsSubcontractor cs
    WHERE ps.isActive = true
    AND cs.status = :contractStatus
    AND ps.isActive = true
    AND (:branchIds IS NULL OR cs.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:responsibleIds IS NULL OR cs.responsible.idUser IN :responsibleIds)
""")
    List<ProviderSubcontractor> findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrue(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("contractStatus") ContractStatusEnum contractStatus
    );
}
