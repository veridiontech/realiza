package bl.tech.realiza.domains.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
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
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<DocumentMatrixSubgroupSnapshot> subgroups;
}
