package bl.tech.realiza.gateways.controllers.interfaces.documents;

import bl.tech.realiza.domains.documents.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, String> {
}
