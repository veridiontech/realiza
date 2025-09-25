package bl.tech.realiza.domains.auditLogs.employee;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.auditLogs.AuditLog;
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
@DiscriminatorValue("EMPLOYEE")
public class AuditLogEmployee extends AuditLog {
    private String employeeId;
    private String employeeFullName;
    private String enterpriseId;
    private String enterpriseCorporateName;
}
