package bl.tech.realiza.domains.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@DiscriminatorValue("SUBCONTRACTOR")
public class ProviderSubcontractorSnapshot extends ProviderSnapshot {
    @ManyToOne
    @JoinColumn(name = "supplierId")
    @JsonBackReference
    private ProviderSupplierSnapshot supplier;

    @OneToMany(mappedBy = "subcontractor")
    @JsonBackReference
    private List<ContractProviderSubcontractorSnapshot> contractsSubcontractor;

    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<EmployeeSnapshot> employees;

    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<DocumentProviderSubcontractorSnapshot> documentsSubcontractor;
}
