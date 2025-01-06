package bl.tech.realiza.domains.users;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class UserSubcontractor extends User {
    @ManyToOne
    private ProviderSubcontractor providerSubcontractor;
}
