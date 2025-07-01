package bl.tech.realiza.domains.auditLogs.user;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.user.User;
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
@DiscriminatorValue("USER")
public class AuditLogUser extends AuditLog {
    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;
}
