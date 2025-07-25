package bl.tech.realiza.domains.services;

import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private SolicitationType solicitationType;
    @Builder.Default
    private Status status = Status.PENDING;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private String invitationToken;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentId")
    @JsonBackReference
    private ContractDocument contractDocument;

    public enum Status {
        APPROVED,
        DENIED,
        PENDING
    }

    public enum SolicitationType {
        CREATION,
        INACTIVATION,
        EXEMPTION
    }
}
