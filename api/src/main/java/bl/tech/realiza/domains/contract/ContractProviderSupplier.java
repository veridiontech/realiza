package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

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

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "idProviderSuppllier", nullable = false)
    private ProviderSupplier providerSupplier;

    @ManyToOne
    @JoinColumn(name = "idBranch", nullable = false)
    private Branch branch;

    @OneToMany(mappedBy = "contractProviderSupplier", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ContractProviderSubcontractor> contractsSubcontractor;
}
