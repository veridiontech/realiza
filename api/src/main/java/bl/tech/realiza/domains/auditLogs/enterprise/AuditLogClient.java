package bl.tech.realiza.domains.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.clients.Client;
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
@DiscriminatorValue("CLIENT")
public class AuditLogClient extends AuditLog {
    private String clientId;
    private String clientCorporateName;
}
