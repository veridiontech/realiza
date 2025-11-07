package bl.tech.realiza.gateways.controllers.interfaces.documents;

import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentPendingResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface DocumentController {
    ResponseEntity<Void> changeDocumentStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto);
    ResponseEntity<String> documentExemption(String documentId, String contractId, String description);
    ResponseEntity<List<DocumentPendingResponseDto>> nonConformingDocumentsByEnterpriseId(String enterpriseId);
    ResponseEntity<String> getVersionByAuditLogId(String auditLogId);
}
