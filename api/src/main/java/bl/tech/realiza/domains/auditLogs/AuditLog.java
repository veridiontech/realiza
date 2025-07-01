package bl.tech.realiza.domains.auditLogs;

import bl.tech.realiza.domains.enums.AuditLogActions;
import bl.tech.realiza.domains.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "entityName")
public abstract class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idRecord;
    private String description;
    private String notes;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private AuditLogActions action;

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;
}
