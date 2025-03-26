package bl.tech.realiza.domains.services;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
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
    @Builder.Default
    private Status status = Status.PENDING;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idRequester")
    private User requester;

    @OneToOne
    @JoinColumn(name = "idNewUser")
    private UserClient newUser;

    @OneToOne
    @JoinColumn(name = "idNewProvider")
    private Provider newProvider;

    public enum Status {
        APPROVED,
        DENIED,
        PENDING
    }
}
