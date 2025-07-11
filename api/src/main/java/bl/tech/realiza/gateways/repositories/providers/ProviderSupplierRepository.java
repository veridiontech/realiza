package bl.tech.realiza.gateways.repositories.providers;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProviderSupplierRepository extends JpaRepository<ProviderSupplier, String> {
    Page<ProviderSupplier> findAllByBranches_IdBranchAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<ProviderSupplier> findAllByBranches_IdBranchAndIsActiveIsTrue(String idSearch);
    Optional<ProviderSupplier> findByCnpj(String cnpj);
    int countByBranches_IdBranchAndIsActiveIsTrue(String idSearch);
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
    AND cs.isActive = Contract.IsActive.ATIVADO
    AND cs.branch.client.idClient = :clientId
    AND ps.isActive = true
""")
    List<ProviderSupplier> findAllByClientIdAndContractIsActiveAndIsActiveIsTrue(
            @Param("clientId") String clientId
    );

    @Query("""
    SELECT ps
    FROM ProviderSupplier ps
    LEFT JOIN ps.contractsSupplier cs
    WHERE ps.isActive = true
    AND cs.isActive = Contract.IsActive.ATIVADO
    AND ps.isActive = true
    AND (:branchIds IS NULL OR cs.branch.idBranch IN :branchIds)
    AND (:responsibleIds IS NULL OR cs.responsible.idUser IN :responsibleIds)
""")
    List<ProviderSupplier> findAllByBranchIdsAndResponsibleIdsAndContractIsActiveAndIsActiveIsTrue(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds
    );

    @Query("""
    SELECT ps
    FROM ProviderSupplier ps
    LEFT JOIN ps.contractsSupplier cs
    WHERE ps.isActive = true
    AND cs.isActive = Contract.IsActive.ATIVADO
    AND ps.isActive = true
    AND (:branchIds IS NULL OR cs.branch.idBranch IN :branchIds)
    AND (:responsibleIds IS NULL OR cs.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    List<ProviderSupplier> findAllByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles
    );
}
