package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProviderSupplierSnapshotRepository extends JpaRepository<ProviderSupplierSnapshot, String> {
    Optional<ProviderSupplierSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idProvider, Date from, SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT COUNT(ps)
    FROM ProviderSupplierSnapshot ps
    JOIN ps.contractsSupplier cs
    WHERE cs.branch.client.id.id = :clientId
        AND cs.branch.client.id.snapshotDate = :date
        AND cs.branch.client.id.frequency = :frequency
""")
    Long countByClientIdDateAndFrequency(@Param("clientId") String clientId,
                                         @Param("date") Date date,
                                         @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT ps
    FROM ProviderSupplierSnapshot ps
    LEFT JOIN ps.contractsSupplier cs
    WHERE cs.status = :contractStatus
        AND cs.branch.client.id.id = :clientId
        AND cs.branch.client.id.snapshotDate = :date
        AND cs.branch.client.id.frequency = :frequency
""")
    List<ProviderSupplierSnapshot> findAllByClientIdAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(
            @Param("clientId") String clientId,
            @Param("contractStatus") ContractStatusEnum contractStatus,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT ps
    FROM ProviderSupplierSnapshot ps
    LEFT JOIN ps.contractsSupplier cs
    WHERE cs.status = :contractStatus
        AND (:branchIds IS NULL OR cs.branch.id.id IN :branchIds)
        AND (:responsibleIds IS NULL OR cs.responsible.id.id IN :responsibleIds)
        AND cs.branch.client.id.snapshotDate = :date
        AND cs.branch.client.id.frequency = :frequency
""")
    List<ProviderSupplierSnapshot> findAllByBranchIdsAndResponsibleIdsAndContractStatusAndIsActiveIsTrueAndDateAndFrequency(
            @Param("branchIds") List<String> branchIds,
            @Param("responsibleIds") List<String> responsibleIds,
            @Param("contractStatus") ContractStatusEnum contractStatus,
            @Param("date") Date date,
            @Param("frequency") SnapshotFrequencyEnum frequency);
}
