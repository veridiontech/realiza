package bl.tech.realiza.domains.auditLogs.ultragaz;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.ultragaz.Market;
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
@DiscriminatorValue("MARKET")
public class AuditLogMarket extends AuditLog {
    @ManyToOne
    @JoinColumn(name = "idMarket")
    private Market idMarket;
}
