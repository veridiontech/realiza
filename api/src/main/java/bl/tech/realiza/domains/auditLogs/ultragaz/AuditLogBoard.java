package bl.tech.realiza.domains.auditLogs.ultragaz;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.ultragaz.Board;
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
@DiscriminatorValue("BOARD")
public class AuditLogBoard extends AuditLog {
    @ManyToOne
    @JoinColumn(name = "idBoard")
    private Board idBoard;
}
