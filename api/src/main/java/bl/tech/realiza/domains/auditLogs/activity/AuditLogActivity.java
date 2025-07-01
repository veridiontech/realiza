package bl.tech.realiza.domains.auditLogs.activity;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.activity.Activity;
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
public class AuditLogActivity extends AuditLog {
    private String activityId;
    private String activityTitle;
    private String branchId;
    private String branchName;
}
