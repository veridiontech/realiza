package bl.tech.realiza.gateways.repositories.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSubcontractorSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractProviderSubcontractorSnapshotRepository extends JpaRepository<ContractProviderSubcontractorSnapshot, String> {
}
