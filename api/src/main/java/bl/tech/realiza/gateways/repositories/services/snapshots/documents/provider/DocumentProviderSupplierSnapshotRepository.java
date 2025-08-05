package bl.tech.realiza.gateways.repositories.services.snapshots.documents.provider;

import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSupplierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentProviderSupplierSnapshotRepository extends JpaRepository<DocumentProviderSupplierSnapshot, String> {
}
