package bl.tech.realiza.domains.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idNotification;
    private String title;
    private String description;
    private Boolean read;
    private Boolean isActive = true;

    @ManyToOne
    private User user;
}
