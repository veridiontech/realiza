package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("SUBCONTRACTOR")
public class ContractProviderSubcontractorSnapshot extends ContractSnapshot {
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "contract_supplier_id", referencedColumnName = "contract_id"),
            @JoinColumn(name = "contract_supplier_frequency", referencedColumnName = "contract_frequency"),
            @JoinColumn(name = "contract_supplier_snapshot_date", referencedColumnName = "contract_snapshot_date")
    })
    @JsonManagedReference
    private ContractProviderSupplierSnapshot contractSupplier;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "subcontractorId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "subcontractorFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "subcontractorSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    @JsonBackReference
    private ProviderSubcontractorSnapshot subcontractor;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "supplierId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "supplierFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "supplierSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    @JsonBackReference
    private ProviderSupplierSnapshot supplier;
}
