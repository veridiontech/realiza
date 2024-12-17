package bl.tech.realiza.gateways.repositories.documents.providers;

import bl.tech.realiza.domains.documents.providers.DocumentsSupplier;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentsSupplierRepository extends JpaRepository<DocumentsSupplier, String> {
}
