package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentProviderSubcontractorRepository extends JpaRepository<DocumentProviderSubcontractor, String> {
    Page<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch, Pageable pageable);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndIsActiveIsTrue(String idSearch);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndDocumentMatrix_SubGroup_Group_GroupNameAndIsActive(String idSearch, String groupName, Boolean isActive);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProviderAndConformingIsFalse(String idProvider);
    List<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IdProvider(String idProvider);

    @Query("""
    SELECT
        COUNT(dps) AS total,
        SUM(CASE WHEN dps.status = :status THEN 1 ELSE 0 END) AS pendentes
    FROM DocumentProviderSubcontractor dps
    JOIN dps.providerSubcontractor psb
    JOIN psb.providerSupplier psp
    JOIN psp.branches b
    WHERE b.idBranch = :branchId
""")
    Object[] countTotalAndPendentesByBranch(
            @Param("branchId") String branchId,
            @Param("status") Document.Status status
    );

    @Query("""
    SELECT
        dps.type, dps.status, COUNT(dps)
    FROM DocumentProviderSubcontractor dps
    JOIN dps.providerSubcontractor psb
    JOIN psb.providerSupplier psp
    JOIN psp.branches b
    WHERE b.idBranch = :branchId
    GROUP BY dps.type, dps.status
""")
    List<Object[]> countTotalTypesByBranch(@Param("branchId") String branchId);

    @Query("""
    SELECT COUNT(d)
    FROM DocumentProviderSubcontractor d
    JOIN d.providerSubcontractor psc
    JOIN psc.providerSupplier ps
    JOIN ps.branches b
    WHERE b.idBranch = :branchId AND d.status = :status
""")
    Long countByBranchIdAndStatus(@Param("branchId") String branchId, @Param("status") Document.Status status);

    @Query("""
    SELECT COUNT(d)
    FROM DocumentProviderSubcontractor d
    JOIN d.providerSubcontractor psc
    JOIN psc.providerSupplier ps
    JOIN ps.branches b
    WHERE b.idBranch = :branchId
""")
    Long countByBranchId(@Param("branchId") String branchId);

    Page<DocumentProviderSubcontractor> findAllByProviderSubcontractor_IsActive(Pageable pageable, Boolean isActive);
}
