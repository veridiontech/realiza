package bl.tech.realiza.domains.auditLogs.document;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.OwnerEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("DOCUMENT")
public class AuditLogDocument extends AuditLog {
    private String documentId;
    private String documentTitle;
    private String fileId;
    private Boolean hasDoc;
    private String ownerId;
    private OwnerEnum owner;
}
