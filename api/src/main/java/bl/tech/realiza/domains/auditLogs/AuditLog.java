package bl.tech.realiza.domains.auditLogs;

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
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User idUser;
}
