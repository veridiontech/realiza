package bl.tech.realiza.gateways.repositories.services.snapshots.clients;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface BranchSnapshotRepository extends JpaRepository<BranchSnapshot, String> {
    Optional<BranchSnapshot> findById_IdAndId_SnapshotDateAndId_Frequency(String idBranch, Date from, SnapshotFrequencyEnum frequency);
}
