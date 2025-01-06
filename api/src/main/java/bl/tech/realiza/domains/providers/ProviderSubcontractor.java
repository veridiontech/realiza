package bl.tech.realiza.domains.providers;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class ProviderSubcontractor extends Provider {
    @OneToOne
    private ProviderSupplier providerSupplier;
}
