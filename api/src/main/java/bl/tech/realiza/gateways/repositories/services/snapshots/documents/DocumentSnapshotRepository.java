package bl.tech.realiza.gateways.repositories.services.snapshots.documents;

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
""")
    Object[] countTotalAndConformitySupplierByClientIdAndResponsibleIdsAndDocumentTypesAndDocumentTitles(
            @Param("clientId") String clientId,
            @Param("providerIds") List<String> providerIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("documentTypes") List<String> documentTypes,
            @Param("documentTitles") List<String> documentTitles

    );
}
