package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProviderSubcontractorSnapshotRepository extends JpaRepository<ProviderSubcontractorSnapshot, String> {
    Optional<ProviderSubcontractorSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idProvider, Date from, SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT ps
    FROM ProviderSubcontractorSnapshot ps
    LEFT JOIN ps.contractsSubcontractor cs
    WHERE cs.status = :contractStatus
        AND cs.contractSupplier.branch.client.id.id = :clientId
        AND cs.subcontractor.id.snapshotDate = :date
        AND cs.subcontractor.id.frequency = :frequency
""")
    List<ProviderSubcontractorSnapshot> findAllByContractSupplierClientIdAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(
            @Param("clientId") String clientId,
            @Param("contractStatus") ContractStatusEnum contractStatus,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT ps
    FROM ProviderSubcontractorSnapshot ps
    LEFT JOIN ps.contractsSubcontractor cs
    WHERE cs.status = :contractStatus
        AND (:branchIds IS NULL OR cs.contractSupplier.branch.id.id IN :branchIds)
        AND (:responsibleIds IS NULL OR cs.responsible.id.id IN :responsibleIds)
        AND cs.subcontractor.id.snapshotDate = :date
        AND cs.subcontractor.id.frequency = :frequency
""")
    List<ProviderSubcontractorSnapshot> findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("contractStatus") ContractStatusEnum contractStatus,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);
}
