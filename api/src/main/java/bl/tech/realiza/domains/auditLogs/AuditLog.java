package bl.tech.realiza.domains.auditLogs;

import bl.tech.realiza.domains.enums.AuditLogActionsEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
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
    private String justification;
    private String notes;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private AuditLogActionsEnum action;

    private String userResponsibleId;
    private String userResponsibleCpf;
    private String userResponsibleFullName;
    private String userResponsibleEmail;
}
