package bl.tech.realiza.domains.services.snapshots.documents.provider;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
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
@DiscriminatorValue("SUBCONTRACTOR")
public class DocumentProviderSubcontractorSnapshot extends DocumentSnapshot {
    @Builder.Default
    private LocalDateTime assignmentDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "subcontractorId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "subcontractorFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "subcontractorSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    private ProviderSubcontractorSnapshot subcontractor;
}
