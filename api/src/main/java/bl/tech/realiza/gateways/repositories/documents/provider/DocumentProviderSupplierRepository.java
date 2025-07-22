package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentProviderSupplierRepository extends JpaRepository<DocumentProviderSupplier, String> {
    Page<DocumentProviderSupplier> findAllByProviderSupplier_IdProviderAndIsActive(String idSearch, Pageable pageable, Boolean isActive);
    List<DocumentProviderSupplier> findAllByProviderSupplier_IdProviderAndIsActive(String idSearch, Boolean isActive);
    List<DocumentProviderSupplier> findAllByProviderSupplier_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(String idSearch, String groupName, Boolean isActive);

    @Query("""
    SELECT
        COUNT(dps) AS total,
        SUM(CASE WHEN dps.status = :status THEN 1 ELSE 0 END) AS pendentes
    FROM DocumentProviderSupplier dps
    JOIN dps.providerSupplier es
    JOIN es.branches b
    WHERE b.idBranch = :branchId
""")
    Object[] countTotalAndPendentesByBranch(
            @Param("branchId") String branchId,
            @Param("status") Document.Status status
    );

    @Query("""
    SELECT
        dps.type, dps.status, COUNT(dps)
    FROM DocumentProviderSupplier dps
    JOIN dps.providerSupplier es
    JOIN es.branches b
    WHERE b.idBranch = :branchId
    GROUP BY dps.type, dps.status
""")
    List<Object[]> countTotalTypesByBranch(@Param("branchId") String branchId);

    @Query("""
    SELECT COUNT(d)
    FROM DocumentProviderSupplier d
    JOIN d.providerSupplier ps
    JOIN ps.branches b
    WHERE b.idBranch = :branchId AND d.status = :status
""")
    Long countByBranchIdAndStatus(@Param("branchId") String branchId, @Param("status") Document.Status status);

    @Query("""
    SELECT COUNT(d)
    FROM DocumentProviderSupplier d
    JOIN d.providerSupplier ps
    JOIN ps.branches b
    WHERE b.idBranch = :branchId
""")
    Long countByBranchId(@Param("branchId") String branchId);

}
