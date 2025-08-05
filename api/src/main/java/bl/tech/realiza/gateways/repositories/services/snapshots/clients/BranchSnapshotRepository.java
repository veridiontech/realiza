package bl.tech.realiza.gateways.repositories.services.snapshots.clients;

import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BranchSnapshotRepository extends JpaRepository<BranchSnapshot, String> {
}
