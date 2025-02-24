package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("SUBCONTRACTOR")
public class ContractProviderSubcontractor extends Contract {
    @ManyToOne(cascade = CascadeType.REMOVE)
    private ContractProviderSupplier contractProviderSupplier;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private ProviderSubcontractor providerSubcontractor;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private ProviderSupplier providerSupplier;
}
