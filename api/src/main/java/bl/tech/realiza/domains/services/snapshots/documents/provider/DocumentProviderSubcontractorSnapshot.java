package bl.tech.realiza.domains.services.snapshots.documents.provider;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private SnapshotFrequencyEnum frequency;

    @ManyToOne
    @JoinColumn(name = "subcontractorId")
    private ProviderSubcontractorSnapshot subcontractor;
}
