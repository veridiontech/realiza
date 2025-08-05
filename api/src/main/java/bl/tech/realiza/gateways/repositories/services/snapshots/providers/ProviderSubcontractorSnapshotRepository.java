package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSubcontractorSnapshotRepository extends JpaRepository<ProviderSubcontractorSnapshot, String> {
}
