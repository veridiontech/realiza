package bl.tech.realiza.gateways.controllers.interfaces.documents;

import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import org.springframework.http.ResponseEntity;

public interface DocumentController {
    ResponseEntity<String> changeDocumentStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto);
    ResponseEntity<String> documentExemption(String documentId, String contractId);
}
