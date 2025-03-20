package bl.tech.realiza.domains.user;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
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

    @ManyToOne
    @JoinColumn(name = "idBranch", nullable = false)
    private Branch branch;
}
