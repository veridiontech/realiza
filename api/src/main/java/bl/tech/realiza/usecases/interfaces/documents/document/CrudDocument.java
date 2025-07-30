package bl.tech.realiza.usecases.interfaces.documents.document;

import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentPendingResponseDto;

import java.util.List;

public interface CrudDocument {
    void expirationChange();
    void expirationCheck();
    String changeStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto);
    String documentExemptionRequest(String documentId, String contractId);
    List<DocumentPendingResponseDto> findNonConformingDocumentByEnterpriseId(String enterpriseId);
    String findVersionByAuditLog(String auditLogId);
    void deleteOldReprovedDocuments();
}
