package bl.tech.realiza.usecases.interfaces.documents.document;

import bl.tech.realiza.domains.documents.Document;

public interface CrudDocument {
    void expirationChange();
    void expirationCheck();
    String changeStatus(String documentId, Document.Status status);
}
