package bl.tech.realiza.gateways.repositories.documents.providers;

import bl.tech.realiza.domains.documents.providers.DocumentSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSupplierRepository extends JpaRepository<DocumentSupplier, String> {
}
