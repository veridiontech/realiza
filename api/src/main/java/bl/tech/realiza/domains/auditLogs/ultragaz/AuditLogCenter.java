package bl.tech.realiza.domains.auditLogs.ultragaz;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.ultragaz.Center;
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
@DiscriminatorValue("CENTER")
public class AuditLogCenter extends AuditLog {
    @ManyToOne
    @JoinColumn(name = "idCenter")
    private Center idCenter;
}
