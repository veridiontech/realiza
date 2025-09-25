package bl.tech.realiza.domains.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.providers.Provider;
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
@DiscriminatorValue("PROVIDER")
public class AuditLogProvider extends AuditLog {
    private String providerId;
    private String providerCorporateName;
}
