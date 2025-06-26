package bl.tech.realiza.domains.auditLogs.document;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.documents.Document;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("DOCUMENT")
public class AuditLogDocument extends AuditLog {
    private AuditLogDocumentActions action;

    @ManyToOne
    @JoinColumn(name = "idDocumentation")
    private Document idDocumentation;

    public enum AuditLogDocumentActions {
        CREATE,
        UPDATE,
        UPLOAD,
        DELETE,
        APPROVE,
        REJECT,
        EXEMPT,
        AI_APPROVE,
        AI_REJECT
    }
}
