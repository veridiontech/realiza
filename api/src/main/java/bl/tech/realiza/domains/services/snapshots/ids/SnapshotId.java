package bl.tech.realiza.domains.services.snapshots.ids;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotId implements Serializable {
    private String id;
    private SnapshotFrequencyEnum frequency;
    private Date snapshotDate;
}
