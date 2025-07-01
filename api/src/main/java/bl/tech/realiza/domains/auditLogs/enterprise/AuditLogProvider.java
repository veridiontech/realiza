package bl.tech.realiza.domains.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.providers.Provider;
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
@DiscriminatorValue("PROVIDER")
public class AuditLogProvider extends AuditLog {
    private String providerId;
    private String providerCorporateName;
}
