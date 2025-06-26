package bl.tech.realiza.usecases.interfaces.documents.document;

import bl.tech.realiza.gateways.requests.documents.DocumentStatusChangeRequestDto;

public interface CrudDocument {
    void expirationChange();
    void expirationCheck();
    String changeStatus(String documentId, DocumentStatusChangeRequestDto documentStatusChangeRequestDto);
    String documentExemption(String documentId, String contractId);
}
