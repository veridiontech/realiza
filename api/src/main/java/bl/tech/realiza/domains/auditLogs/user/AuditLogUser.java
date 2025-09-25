package bl.tech.realiza.domains.auditLogs.user;

import bl.tech.realiza.domains.auditLogs.AuditLog;
import bl.tech.realiza.domains.user.User;
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
@DiscriminatorValue("USER")
public class AuditLogUser extends AuditLog {
    private String userId;
    private String userCpf;
    private String userFullName;
    private String userEmail;
}
