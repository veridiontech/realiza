package bl.tech.realiza.gateways.repositories.documents;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentStatusEnum;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
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
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE (:clientId IS NULL OR cps.branch.client.idClient = :clientId)
    AND (:providerIds IS NULL OR cps.providerSupplier.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndConformitySupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (:clientId IS NULL OR cpsb.contractProviderSupplier.branch.client.idClient = :clientId)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds 
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndConformitySubcontractorByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );


    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE (:branchIds IS NULL OR cps.branch.idBranch IN :branchIds)
    AND (:providerIds IS NULL OR cps.providerSupplier.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (:branchIds IS NULL OR cpsb.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
SELECT COUNT(DISTINCT d)
FROM Document d
JOIN d.contractDocuments cd
JOIN cd.contract c
LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
WHERE cps.branch.idBranch IN :branchIds
AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
AND (:providerIds IS NULL OR cps.providerSupplier.idProvider IN :providerIds)
AND d.type = :type
AND d.status = :status
AND (:documentTitles IS null OR d.title IN :documentTitles)
""")
    Long countSupplierByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("type") String type,
            @Param("status") Document.Status status,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTitles") List<String> documentTitles
    );

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (cpsb.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds 
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Long countSubcontractorByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(@Param("branchIds") List<String> branchIds,
                                                                            @Param("providerIds") List<String> providerIds,
                                                                            @Param("type") String type,
                                                                            @Param("status") Document.Status status,
                                                                            @Param("responsibleIds") List<String> responsibleIds,
                                                                            @Param("documentTitles") List<String> documentTitles
    );

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE cps.branch.client.idClient = :clientId
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:providerIds IS NULL OR cps.providerSupplier.idProvider IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Long countSupplierByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(@Param("clientId") String clientId,
                                                                           @Param("providerIds") List<String> providerIds,
                                                                           @Param("type") String type,
                                                                           @Param("status") Document.Status status,
                                                                           @Param("responsibleIds") List<String> responsibleIds,
                                                                           @Param("documentTitles") List<String> documentTitles);

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE cpsb.contractProviderSupplier.branch.client.idClient = :clientId
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds 
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Long countSubcontractorByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitles(@Param("clientId") String clientId,
                                                                           @Param("providerIds") List<String> providerIds,
                                                                           @Param("type") String type,
                                                                           @Param("status") Document.Status status,
                                                                           @Param("responsibleIds") List<String> responsibleIds,
                                                                           @Param("documentTitles") List<String> documentTitles);

    @Query("""
    SELECT d.type
    FROM Document d
    GROUP BY d.type
""")
    List<String> findDistinctDocumentType();

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE (:clientId IS NULL OR cps.branch.client.idClient = :clientId)
    AND (:providerIds IS NULL OR cps.providerSupplier.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndAdherenceSupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (:clientId IS NULL OR cpsb.contractProviderSupplier.branch.client.idClient = :clientId)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndAdherenceSubcontractorByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE (:branchIds IS NULL OR cps.branch.idBranch IN :branchIds)
    AND (:providerIds IS NULL OR cps.providerSupplier.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndAdherenceSupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (:branchIds IS NULL OR cpsb.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndAdherenceSubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE (cps.providerSupplier.idProvider = :providerId)
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndAdherenceByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles
    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplier) cps
    WHERE (cps.providerSupplier.idProvider = :providerId)
    AND (:responsibleIds IS NULL OR cps.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndConformityByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles
    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE cpsb.contractProviderSupplier.providerSupplier.idProvider = :providerId
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndAdherenceByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles
    );

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM Document d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE cpsb.contractProviderSupplier.providerSupplier.idProvider = :providerId
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
""")
    Object[] countTotalAndConformityByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles
    );

    List<Document> findAllByDocumentMatrix_IdDocument(String idDocument);

    @Query("""
    SELECT d
    FROM Document d
    LEFT JOIN d.contractDocuments cd
    WHERE d.validity = :validity
        AND cd.contract.status = :status
""")
    List<Document> findAllByValidityAndContractStatus(@Param("validity") DocumentValidityEnum validity,
                                                      @Param("status") ContractStatusEnum status);
}
