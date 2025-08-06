package bl.tech.realiza.domains.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentMatrixGroupSnapshot {
    @EmbeddedId
    private SnapshotId id;
    private String name;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<DocumentMatrixSubgroupSnapshot> subgroups;
}
