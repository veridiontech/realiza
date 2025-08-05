package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
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
public class ContractProviderSupplierSnapshot extends ContractSnapshot {

    @ManyToOne
    @JoinColumn(name = "providerId")
    @JsonManagedReference
    private ProviderSupplierSnapshot supplier;

    @ManyToOne
    @JoinColumn(name = "branchId")
    @JsonManagedReference
    private BranchSnapshot branch;

    @OneToMany(mappedBy = "contractSupplier")
    @JsonManagedReference
    private List<ContractProviderSubcontractorSnapshot> subcontract;
}
