package bl.tech.realiza.domains.services.snapshots.documents.provider;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUPPLIER")
public class DocumentProviderSupplierSnapshot extends DocumentSnapshot {
    @Builder.Default
    private LocalDateTime assignmentDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "supplierId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "supplierFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "supplierSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    @JsonManagedReference
    private ProviderSupplierSnapshot supplier;
}
