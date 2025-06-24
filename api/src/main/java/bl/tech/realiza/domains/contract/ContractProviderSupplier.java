package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @Builder.Default
    private Boolean subcontractPermission = true;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idServiceType")
    private ServiceTypeBranch serviceTypeBranch;

    @ManyToOne
    @JoinColumn(name = "idProviderSupplier")
    private ProviderSupplier providerSupplier;

    @ManyToOne
    @JoinColumn(name = "idBranch")
    private Branch branch;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToMany(mappedBy = "contractProviderSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSubcontractor> contractsSubcontractor;
}
