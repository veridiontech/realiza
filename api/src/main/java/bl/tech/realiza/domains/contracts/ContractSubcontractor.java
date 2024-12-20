package bl.tech.realiza.domains.contracts;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
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
@DiscriminatorValue("SUBCONTRACTOR")
public class ContractSubcontractor extends Contract {
    private String contract_reference;
    @ManyToOne
    private ProviderSubcontractor providerSubcontractor;
}
