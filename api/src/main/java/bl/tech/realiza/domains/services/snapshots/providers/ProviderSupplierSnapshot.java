package bl.tech.realiza.domains.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.clients.BranchSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@DiscriminatorValue("SUPPLIER")
public class ProviderSupplierSnapshot extends ProviderSnapshot {
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "branchId", referencedColumnName = "id"),
            @JoinColumn(name = "branchFrequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "branchSnapshotDate", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private BranchSnapshot branch;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ProviderSubcontractorSnapshot> subcontractors;

    @OneToMany(mappedBy = "supplier")
    @JsonBackReference
    private List<ContractProviderSupplierSnapshot> contractsSupplier;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractProviderSubcontractorSnapshot> contractsSubcontractor;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<EmployeeSnapshot> employees;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<DocumentProviderSupplierSnapshot> documentsSupplier;
}
