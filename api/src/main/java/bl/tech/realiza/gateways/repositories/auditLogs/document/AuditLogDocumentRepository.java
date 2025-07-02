package bl.tech.realiza.gateways.repositories.auditLogs.document;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogDocumentRepository extends JpaRepository<AuditLogDocument, String> {
    Page<AuditLogDocument> findAllByDocumentId(String id, Pageable pageable);
    Page<AuditLogDocument> findAllByDocumentIdAndAction(String id, AuditLogActionsEnum action, Pageable pageable);

    Page<AuditLogDocument> findAllByDocumentIdAndUserResponsibleId(String id, String idUser, Pageable pageable);

    Page<AuditLogDocument> findAllByDocumentIdAndActionAndUserResponsibleId(String id, AuditLogActionsEnum action, String idUser, Pageable pageable);
}
