package bl.tech.realiza.domains.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USER_NOTIFICATION")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idNotification;
    private String title;
    private String description;
    @Builder.Default
    private Boolean isRead = false;
    private LocalDateTime readAt;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idUser")
    private User user;
}
