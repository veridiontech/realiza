package bl.tech.realiza.gateways.controllers.interfaces.documents;

import bl.tech.realiza.domains.documents.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface DocumentRepository extends JpaRepository<Document, String> {
    Collection<Document> findAllByRequestIs(Document.Request request);
}
