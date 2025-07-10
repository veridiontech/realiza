package bl.tech.realiza.gateways.repositories.documents;

import bl.tech.realiza.domains.documents.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, String> {
    Page<Document> findAllByStatus(Document.Status status, Pageable pageable);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contracts c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (
        :clientId IS NULL OR
        cps.branch.client.idClient = :clientId OR
        cpsb.contractProviderSupplier.branch.client.idClient = :clientId
    )
    AND (
        :responsibleIds IS NULL OR
        cps.responsible.idUser IN :responsibleIds OR
        cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds
    )
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
""")
    Object[] countConformityByGeneralClientFilters(
            @Param("clientId") String clientId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes
    );


    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contracts c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (
        :clientId IS NULL OR
        cps.branch.idBranch IN :branchIds OR
        cpsb.contractProviderSupplier.branch.idBranch = :branchIds
    )
    AND (
        :responsibleIds IS NULL OR
        cps.responsible.idUser IN :responsibleIds OR
        cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds
    )
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
""")
    Object[] countConformityByGeneralBranchFilters(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes
    );

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM Document d
    JOIN d.contracts c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (cps.branch.idBranch IN :branchIds OR cpsb.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:responsible IS NULL OR cps.responsible.idUser IN :responsibleIds OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND d.type = :types
    AND d.status = :status
""")
    Long countByBranchIdsAndTypeAndStatusAndResponsibleIds(@Param("branchIds") List<String> branchIds,
                                                           @Param("type") String type,
                                                           @Param("status") Document.Status status,
                                                           @Param("responsibleIds") List<String> responsibleIds);

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM Document d
    JOIN d.contracts c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (cps.branch.client.idClient = :clientId OR cpsb.contractProviderSupplier.branch.client.idClient = :clientId)
    AND (:responsible IS NULL OR cps.responsible.idUser IN :responsibleIds OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND d.type = :types
    AND d.status = :status
""")
    Long countByClientIdAndTypeAndStatusAndResponsibleIds(@Param("clientId") String clientId,
                                                          @Param("type") String type,
                                                          @Param("status") Document.Status status,
                                                          @Param("responsibleIds") List<String> responsibleIds);
    @Query("""
    SELECT d.type
    FROM Document d
    GROUP BY d.type
""")
    List<String> findDistinctDocumentType();
}
