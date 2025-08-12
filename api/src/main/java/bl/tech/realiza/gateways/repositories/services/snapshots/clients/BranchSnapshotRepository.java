package bl.tech.realiza.gateways.repositories.services.snapshots.clients;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface BranchSnapshotRepository extends JpaRepository<BranchSnapshot, String> {
    Optional<BranchSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idBranch, Date from, SnapshotFrequencyEnum frequency);

    @Query("""
    SELECT b.id.id
    FROM BranchSnapshot b
    WHERE b.client.id.id = :clientId
        AND b.id.snapshotDate = :date
        AND b.id.frequency = :frequency
""")
    List<String> findAllBranchIdsByClientIdAndDateAndFrequency(@Param("clientId") String clientId,
                                            @Param("date") Date date,
                                            @Param("frequency") SnapshotFrequencyEnum frequency);
}
