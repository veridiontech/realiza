package bl.tech.realiza.domains.services;

import bl.tech.realiza.domains.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ItemManagement {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idSolicitation;
    private String title;
    private String details;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User requester;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @ManyToOne(cascade = CascadeType.REMOVE)
    private User newUser;
}
