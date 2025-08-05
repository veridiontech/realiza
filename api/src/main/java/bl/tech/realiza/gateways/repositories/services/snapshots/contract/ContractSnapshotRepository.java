package bl.tech.realiza.gateways.repositories.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractSnapshotRepository extends JpaRepository<ContractSnapshot, String> {
}
