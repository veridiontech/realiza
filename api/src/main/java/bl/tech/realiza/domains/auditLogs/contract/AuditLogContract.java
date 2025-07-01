package bl.tech.realiza.domains.auditLogs.contract;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.contract.Contract;
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
@DiscriminatorValue("CONTRACT")
public class AuditLogContract extends AuditLog {
    @ManyToOne
    @JoinColumn(name = "idContract")
    private Contract contract;
}
