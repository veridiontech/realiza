package bl.tech.realiza.gateways.repositories.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.contract.ContractDocumentSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractDocumentSnapshotRepository extends JpaRepository<ContractDocumentSnapshot, String> {
}
