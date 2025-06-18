package bl.tech.realiza.gateways.controllers.interfaces.documents;

import bl.tech.realiza.domains.documents.Document.Status;
import org.springframework.http.ResponseEntity;

public interface DocumentController {
    ResponseEntity<String> changeDocumentStatus(String documentId, Status status);
}
