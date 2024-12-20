package bl.tech.realiza.domains.contracts;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("SUPPLIER")
public class ContractSupplier extends Contract {
    @ManyToOne
    private ProviderSupplier providerSupplier;
}
