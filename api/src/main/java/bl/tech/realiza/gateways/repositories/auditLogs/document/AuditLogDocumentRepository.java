package bl.tech.realiza.gateways.repositories.auditLogs.document;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogDocumentRepository extends JpaRepository<AuditLogDocument, String> {
}
