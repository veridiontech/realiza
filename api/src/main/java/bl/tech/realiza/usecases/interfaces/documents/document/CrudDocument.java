package bl.tech.realiza.usecases.interfaces.documents.document;

import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;
import bl.tech.realiza.gateways.responses.documents.DocumentPendingResponseDto;

import java.util.List;

public interface CrudDocument {
    void expirationChange();
    void expirationCheck();
    void deleteReprovedCheck();
    String changeStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto);
    String documentExemptionRequest(String documentId, String contractId, String description);
    List<DocumentPendingResponseDto> findNonConformingDocumentByEnterpriseId(String enterpriseId);
    String findVersionByAuditLog(String auditLogId);
    void deleteOldReprovedDocuments();
    void documentValidityCheck(DocumentValidityEnum documentValidityEnum);

    void deleteOverwrittenDocuments();
}
