package bl.tech.realiza.domains.auditLogs.employee;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.auditLogs.AuditLog;
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
@DiscriminatorValue("EMPLOYEE")
public class AuditLogEmployee extends AuditLog {
    @ManyToOne
    @JoinColumn(name = "idEmployee")
    private Employee employee;
}
