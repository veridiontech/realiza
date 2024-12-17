package bl.tech.realiza.domains.users;

import bl.tech.realiza.domains.clients.Client;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIENT")
public class UserClient extends User {
    @ManyToOne
    private Client client;
}
