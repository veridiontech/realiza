package bl.tech.realiza.domains.services.snapshots.providers;

import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.provider.DocumentProviderSupplierSnapshot;
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
@DiscriminatorValue("SUPPLIER")
public class ProviderSupplierSnapshot extends ProviderSnapshot {

    @OneToMany(mappedBy = "supplier")
    @JsonBackReference
    private List<ContractProviderSupplierSnapshot> contractsSupplier;

    @OneToMany(mappedBy = "supplier")
    @JsonBackReference
    private List<ContractProviderSubcontractorSnapshot> contractsSubcontractor;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<EmployeeSnapshot> employees;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<DocumentProviderSupplierSnapshot> documentsSupplier;
}
