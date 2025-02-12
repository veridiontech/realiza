package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.clients.Branch;
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
@DiscriminatorValue("SUPPLIER")
public class ContractProviderSupplier extends Contract {
    private Boolean subcontractPermission;
    @ManyToOne(cascade = CascadeType.ALL)
    private ProviderSupplier providerSupplier;
    @ManyToOne(cascade = CascadeType.ALL)
    private Branch branch;
}
