package bl.tech.realiza.domains.contract;

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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("SUBCONTRACTOR")
public class ContractProviderSubcontractor extends Contract {
    @ManyToOne
    private ProviderSubcontractor providerSubcontractor;
}
