package bl.tech.realiza.domains.auditLogs.serviceType;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
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
@DiscriminatorValue("ACTIVITY")
public class AuditLogServiceType extends AuditLog {
    private String serviceTypeId;
    private String serviceTypeTitle;
    private String branchId;
    private String branchName;
}
