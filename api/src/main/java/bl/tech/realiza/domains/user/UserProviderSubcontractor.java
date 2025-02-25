package bl.tech.realiza.domains.user;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class UserProviderSubcontractor extends User {
    @ManyToOne
    @JoinColumn(name = "idProviderSubcontractor", nullable = false)
    private ProviderSubcontractor providerSubcontractor;
}
