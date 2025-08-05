package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
@DiscriminatorValue("SUBCONTRACTOR")
public class ContractProviderSubcontractorSnapshot extends ContractSnapshot {
    @ManyToOne
    @JoinColumn(name = "contractSupplierId")
    @JsonBackReference
    private ContractProviderSupplierSnapshot contractSupplier;

    @ManyToOne
    @JoinColumn(name = "subcontractorId")
    @JsonBackReference
    private ProviderSubcontractorSnapshot subcontractor;

    @ManyToOne
    @JoinColumn(name = "supplierId")
    @JsonBackReference
    private ProviderSupplierSnapshot supplier;
}
