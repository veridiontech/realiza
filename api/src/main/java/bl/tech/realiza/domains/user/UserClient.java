package bl.tech.realiza.domains.user;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.services.ItemManagement;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIENT")
public class UserClient extends User {
    @Builder.Default
    private Boolean denied = false;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idBranch")
    private Branch branch;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToOne(mappedBy = "newUser", cascade = CascadeType.REMOVE)
    private ItemManagement newUserSolicitation;
}
