package bl.tech.realiza.gateways.repositories.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSupplierSnapshotRepository extends JpaRepository<ProviderSupplierSnapshot, String> {
}
