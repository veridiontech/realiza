package bl.tech.realiza.domains.services.snapshots.providers;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TYPE")
public abstract class ProviderSnapshot {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "provider_id")),
            @AttributeOverride(name = "frequency", column = @Column(name = "provider_frequency")),
            @AttributeOverride(name = "snapshotDate", column = @Column(name = "provider_snapshot_date"))
    })
    private SnapshotId id;
    private String corporateName; // razão social
    private String tradeName; // nome fantasia
    private String cnpj;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
}
