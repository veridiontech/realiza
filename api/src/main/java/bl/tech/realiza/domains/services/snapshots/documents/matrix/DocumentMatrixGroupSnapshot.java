package bl.tech.realiza.domains.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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

//    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonBackReference
//    private List<DocumentMatrixSubgroupSnapshot> subgroups;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<DocumentMatrixSnapshot> documents;
}
