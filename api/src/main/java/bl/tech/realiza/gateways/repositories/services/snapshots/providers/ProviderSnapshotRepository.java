package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.providers.ProviderSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSnapshotRepository extends JpaRepository<ProviderSnapshot, String> {
}
