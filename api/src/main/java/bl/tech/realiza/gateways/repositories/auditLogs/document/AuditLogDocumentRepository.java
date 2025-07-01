package bl.tech.realiza.gateways.repositories.auditLogs.document;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.enums.AuditLogActions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogDocumentRepository extends JpaRepository<AuditLogDocument, String> {
    Page<AuditLogDocument> findAllByDocument_IdDocumentation(String id, Pageable pageable);
    Page<AuditLogDocument> findAllByDocument_IdDocumentationAndAction(String id, AuditLogActions action, Pageable pageable);
}
