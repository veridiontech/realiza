package bl.tech.realiza.gateways.repositories.services.snapshots.documents;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface DocumentSnapshotRepository extends JpaRepository<DocumentSnapshot, String> {
    Optional<DocumentSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idDocumentation, Date from, SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE (:clientId IS NULL OR cps.branch.client.id = :clientId)
    AND (:providerIds IS NULL OR cps.supplier.id IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndConformitySupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE (:clientId IS NULL OR cpsb.contractSupplier.branch.client.id.id = :clientId)
    AND (:providerIds IS NULL OR cpsb.contractSupplier.supplier.id.id IN :providerIds
        OR cpsb.subcontractor.id.id IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractSupplier.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndConformitySubcontractorByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE (:branchIds IS NULL OR cps.branch.id.id IN :branchIds)
    AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndConformitySupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE (:branchIds IS NULL OR cpsb.contractSupplier.branch.id.id IN :branchIds)
    AND (:providerIds IS NULL OR cpsb.contractSupplier.supplier.id.id IN :providerIds
        OR cpsb.subcontractor.id.id IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractSupplier.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndConformitySubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT d.type
    FROM DocumentSnapshot d
    WHERE d.id.snapshotDate = :date
        AND d.id.frequency = :frequency
    GROUP BY d.type
""")
    List<String> findDistinctDocumentType(@Param("date") Date date,
                                          @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE cps.branch.id.id IN :branchIds
    AND (:responsibleIds IS NULL OR cps.responsible.id.id IN :responsibleIds)
    AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS null OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Long countSupplierByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("type") String type,
            @Param("status") Document.Status status,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE (cpsb.contractSupplier.branch.id.id IN :branchIds)
    AND (:responsibleIds IS NULL OR cpsb.contractSupplier.responsible.id.id IN :responsibleIds)
    AND (:providerIds IS NULL OR cpsb.contractSupplier.supplier.id.id IN :providerIds
        OR cpsb.subcontractor.id.id IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Long countSubcontractorByBranchIdsAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(@Param("branchIds") List<String> branchIds,
                                                                                         @Param("providerIds") List<String> providerIds,
                                                                                         @Param("type") String type,
                                                                                         @Param("status") Document.Status status,
                                                                                         @Param("responsibleIds") List<String> responsibleIds,
                                                                                         @Param("documentTitles") List<String> documentTitles,
                                                                                         @Param("date") Date date,
                                                                                         @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE cps.branch.client.id.id = :clientId
    AND (:responsibleIds IS NULL OR cps.responsible.id.id IN :responsibleIds)
    AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Long countSupplierByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(@Param("clientId") String clientId,
                                                                                   @Param("providerIds") List<String> providerIds,
                                                                                   @Param("type") String type,
                                                                                   @Param("status") Document.Status status,
                                                                                   @Param("responsibleIds") List<String> responsibleIds,
                                                                                   @Param("documentTitles") List<String> documentTitles,
                                                                                   @Param("date") Date date,
                                                                                   @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE cpsb.contractSupplier.branch.client.id.id = :clientId
    AND (:responsibleIds IS NULL OR cpsb.contractSupplier.responsible.id.id IN :responsibleIds)
    AND (:providerIds IS NULL OR cpsb.contractSupplier.supplier.id.id IN :providerIds
        OR cpsb.subcontractor.id.id IN :providerIds)
    AND d.type = :type
    AND d.status = :status
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Long countSubcontractorByClientIdAndTypeAndStatusAndResponsibleIdsAndDocumentTitlesAndDateAndFrequency(@Param("clientId") String clientId,
                                                                                        @Param("providerIds") List<String> providerIds,
                                                                                        @Param("type") String type,
                                                                                        @Param("status") Document.Status status,
                                                                                        @Param("responsibleIds") List<String> responsibleIds,
                                                                                        @Param("documentTitles") List<String> documentTitles,
                                                                                        @Param("date") Date date,
                                                                                        @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE (:branchIds IS NULL OR cps.branch.id.id IN :branchIds)
    AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
    AND (:responsibleIds IS NULL OR cps.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndAdherenceSupplierByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractor) cpsb
    WHERE (:branchIds IS NULL OR cpsb.contractProviderSupplier.branch.idBranch IN :branchIds)
    AND (:providerIds IS NULL OR cpsb.contractProviderSupplier.providerSupplier.idProvider IN :providerIds
        OR cpsb.providerSubcontractor.idProvider IN :providerIds)
    AND (:responsibleIds IS NULL OR cpsb.contractProviderSupplier.responsible.idUser IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndAdherenceSubcontractorByBranchIdsAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE (cps.supplier.id.id = :providerId)
    AND (:responsibleIds IS NULL OR cps.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndAdherenceByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSupplierSnapshot ) cps
    WHERE (cps.supplier.id.id = :providerId)
    AND (:responsibleIds IS NULL OR cps.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndConformityByProviderSupplierIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.adherent = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE cpsb.contractSupplier.supplier.id.id = :providerId
    AND (:responsibleIds IS NULL OR cpsb.contractSupplier.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndAdherenceByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(DISTINCT d),
           SUM(CASE WHEN d.conforming = TRUE THEN 1 ELSE 0 END)
    FROM DocumentSnapshot d
    JOIN d.contractDocuments cd
    JOIN cd.contract c
    LEFT JOIN TREAT(c AS ContractProviderSubcontractorSnapshot ) cpsb
    WHERE cpsb.contractSupplier.supplier.id.id = :providerId
    AND (:responsibleIds IS NULL OR cpsb.contractSupplier.responsible.id.id IN :responsibleIds)
    AND (:documentTypes IS NULL OR d.type IN :documentTypes)
    AND (:documentTitles IS NULL OR d.title IN :documentTitles)
    AND d.id.snapshotDate = :date
    AND d.id.frequency = :frequency
""")
    Object[] countTotalAndConformityByProviderSubcontractorIdAndResponsibleIdsAndDocumentTypesAndDocumentTitlesAndDateAndFrequency(
            @Param("providerId") String providerId,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT d
    FROM DocumentSnapshot d
    LEFT JOIN d.contractDocuments cd
    LEFT JOIN TREAT(cd.contract AS ContractProviderSupplierSnapshot ) cps
    WHERE cps.branch.client.id.id = :clientId
        AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
        AND (:documentTypes IS NULL OR d.type IN :documentTypes)
        AND (:responsibleIds IS NULL OR cps.responsible IN :responsibleIds)
        AND (:activeContract IS NULL OR cps.status IN :activeContract)
        AND (:statuses IS NULL OR d.status IN :statuses)
        AND (:documentTitles IS NULL OR d.title IN :documentTitles)
        AND d.id.snapshotDate = :date
        AND d.id.frequency = :frequency
""")
    List<DocumentSnapshot> findAllSupplierByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitlesAndDateAndFrequency(@Param("clientId") String clientId,
                                                                                                                       @Param("providerIds") List<String> providerIds,
                                                                                                                       @Param("documentTypes") List<String> documentTypes,
                                                                                                                       @Param("responsibleIds") List<String> responsibleIds,
                                                                                                                       @Param("activeContract") List<ContractStatusEnum> activeContract,
                                                                                                                       @Param("statuses") List<Document.Status> statuses,
                                                                                                                       @Param("documentTitles") List<String> documentTitles,
                                                                                                                       @Param("date") Date date,
                                                                                                                       @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT d
    FROM DocumentSnapshot d
    LEFT JOIN d.contractDocuments cd
    LEFT JOIN TREAT(cd.contract AS ContractProviderSubcontractorSnapshot ) cps
    WHERE cps.contractSupplier.branch.client.id.id = :clientId
        AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
        AND (:documentTypes IS NULL OR d.type IN :documentTypes)
        AND (:responsibleIds IS NULL OR cps.responsible IN :responsibleIds)
        AND (:activeContract IS NULL OR cps.status IN :activeContract)
        AND (:statuses IS NULL OR d.status IN :statuses)
        AND (:documentTitles IS NULL OR d.title IN :documentTitles)
        AND d.id.snapshotDate = :date
        AND d.id.frequency = :frequency
""")
    List<DocumentSnapshot> findAllSubcontractorByClientIdAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitlesAndDateAndFrequency(@Param("clientId") String clientId,
                                                                                                                            @Param("providerIds") List<String> providerIds,
                                                                                                                            @Param("documentTypes") List<String> documentTypes,
                                                                                                                            @Param("responsibleIds") List<String> responsibleIds,
                                                                                                                            @Param("activeContract") List<ContractStatusEnum> activeContract,
                                                                                                                            @Param("statuses") List<Document.Status> statuses,
                                                                                                                            @Param("documentTitles") List<String> documentTitles,
                                                                                                                            @Param("date") Date date,
                                                                                                                            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT d
    FROM DocumentSnapshot d
    LEFT JOIN d.contractDocuments cd
    LEFT JOIN TREAT(cd.contract AS ContractProviderSupplierSnapshot ) cps
    WHERE (:branchIds IS NULL OR cps.branch.id.id IN :branchIds)
        AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
        AND (:documentTypes IS NULL OR d.type IN :documentTypes)
        AND (:responsibleIds IS NULL OR cps.responsible IN :responsibleIds)
        AND (:activeContract IS NULL OR cps.status IN :activeContract)
        AND (:statuses IS NULL OR d.status IN :statuses)
        AND (:documentTitles IS NULL OR d.title IN :documentTitles)
        AND d.id.snapshotDate = :date
        AND d.id.frequency = :frequency
""")
    List<DocumentSnapshot> findAllSupplierByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitlesAndDateAndFrequency(@Param("branchIds") List<String> branchIds,
                                                                                                                        @Param("providerIds") List<String> providerIds,
                                                                                                                        @Param("documentTypes") List<String> documentTypes,
                                                                                                                        @Param("responsibleIds") List<String> responsibleIds,
                                                                                                                        @Param("activeContract") List<ContractStatusEnum> activeContract,
                                                                                                                        @Param("statuses") List<Document.Status> statuses,
                                                                                                                        @Param("documentTitles") List<String> documentTitles,
                                                                                                                        @Param("date") Date date,
                                                                                                                        @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT d
    FROM DocumentSnapshot d
    LEFT JOIN d.contractDocuments cd
    LEFT JOIN TREAT(cd.contract AS ContractProviderSubcontractorSnapshot ) cps
    WHERE (:branchIds IS NULL OR cps.contractSupplier.branch.id.id IN :branchIds)
        AND (:providerIds IS NULL OR cps.supplier.id.id IN :providerIds)
        AND (:documentTypes IS NULL OR d.type IN :documentTypes)
        AND (:responsibleIds IS NULL OR cps.responsible IN :responsibleIds)
        AND (:activeContract IS NULL OR cps.status IN :activeContract)
        AND (:statuses IS NULL OR d.status IN :statuses)
        AND (:documentTitles IS NULL OR d.title IN :documentTitles)
        AND d.id.snapshotDate = :date
        AND d.id.frequency = :frequency
""")
    List<DocumentSnapshot> findAllSubcontractorByBranchIdsAndProviderIdsAndTypesAndResponsibleIdsAndActiveContractAndStatusAndTitles(@Param("branchIds") List<String> branchIds,
                                                                                                                             @Param("providerIds") List<String> providerIds,
                                                                                                                             @Param("documentTypes") List<String> documentTypes,
                                                                                                                             @Param("responsibleIds") List<String> responsibleIds,
                                                                                                                             @Param("activeContract") List<ContractStatusEnum> activeContract,
                                                                                                                             @Param("statuses") List<Document.Status> statuses,
                                                                                                                             @Param("documentTitles") List<String> documentTitles,
                                                                                                                             @Param("date") Date date,
                                                                                                                             @Param("frequency") SnapshotFrequencyEnum frequency);
}
