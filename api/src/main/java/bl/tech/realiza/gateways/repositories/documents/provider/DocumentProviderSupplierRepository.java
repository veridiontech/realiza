package bl.tech.realiza.gateways.repositories.documents.provider;

import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentProviderSupplierRepository extends JpaRepository<DocumentProviderSupplier, String> {
}
