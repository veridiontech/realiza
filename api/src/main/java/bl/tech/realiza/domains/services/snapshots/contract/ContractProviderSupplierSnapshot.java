package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("SUPPLIER")
public class ContractProviderSupplierSnapshot extends ContractSnapshot {

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "supplier_id", referencedColumnName = "provider_id"),
            @JoinColumn(name = "supplier_frequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "supplier_snapshot_date", referencedColumnName = "provider_snapshot_date")
    })
    @JsonManagedReference
    private ProviderSupplierSnapshot supplier;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "branch_id", referencedColumnName = "id"),
            @JoinColumn(name = "branch_frequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "branch_snapshot_date", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private BranchSnapshot branch;

    @OneToMany(mappedBy = "contractSupplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractProviderSubcontractorSnapshot> subcontract;
}
