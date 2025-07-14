package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderSubcontractorRepository extends JpaRepository<ProviderSubcontractor, String> {
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
    AND cs.isActive = :contractStatus
    AND cs.contractProviderSupplier.branch.client.idClient = :clientId
    AND ps.isActive = true
""")
    List<ProviderSubcontractor> findAllByContractSupplierClientIdAndContractIsActiveAndIsActiveIsTrue(
            @Param("clientId") String clientId,
            @Param("contractStatus") Contract.IsActive contractStatus
    );

    @Query("""
    SELECT ps
    FROM ProviderSubcontractor ps
    LEFT JOIN ps.contractsSubcontractor cs
    WHERE ps.isActive = true
    AND cs.isActive = :contractStatus
    AND ps.isActive = true
    AND (:branchIds IS NULL OR cs.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:responsibleIds IS NULL OR cs.responsible.idUser IN :responsibleIds)
""")
    List<ProviderSubcontractor> findAllByBranchIdsAndResponsibleIdsAndContractIsActiveAndIsActiveIsTrue(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("contractStatus") Contract.IsActive contractStatus
    );
}
