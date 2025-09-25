package bl.tech.realiza.domains.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class ProviderSubcontractorSnapshot extends ProviderSnapshot {
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "supplierId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "supplierFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "supplierSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    @JsonManagedReference
    private ProviderSupplierSnapshot supplier;

    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractProviderSubcontractorSnapshot> contractsSubcontractor;

    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<EmployeeSnapshot> employees;

    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<DocumentProviderSubcontractorSnapshot> documentsSubcontractor;
}
