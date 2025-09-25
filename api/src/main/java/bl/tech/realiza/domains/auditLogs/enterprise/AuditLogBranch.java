package bl.tech.realiza.domains.auditLogs.enterprise;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.clients.Branch;
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
@DiscriminatorValue("BRANCH")
public class AuditLogBranch extends AuditLog {
    private String branchId;
    private String branchName;
}
