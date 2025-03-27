package bl.tech.realiza.domains.services;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idRequester")
    @JsonBackReference // Evita serialização recursiva
    private User requester;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idNewUser")
    private User newUser;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idNewProvider")
    private Provider newProvider;

    public enum Status {
        APPROVED,
        DENIED,
        PENDING
    }
}
