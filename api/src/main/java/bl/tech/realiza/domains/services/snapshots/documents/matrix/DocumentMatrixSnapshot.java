package bl.tech.realiza.domains.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class DocumentMatrixSnapshot {
    @EmbeddedId
    private SnapshotId id;
    private String name;
    private String type;
    @Builder.Default
    private Boolean isUnique = true;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

//    @ManyToOne
//    @JoinColumns({
//            @JoinColumn(name = "subgroupId", referencedColumnName = "id"),
//            @JoinColumn(name = "subgroupFrequency", referencedColumnName = "frequency"),
//            @JoinColumn(name = "subgroupSnapshotDate", referencedColumnName = "snapshotDate")
//    })
//    @JsonManagedReference
//    private DocumentMatrixSubgroupSnapshot subgroup;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "groupId", referencedColumnName = "id"),
            @JoinColumn(name = "groupFrequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "groupSnapshotDate", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private DocumentMatrixGroupSnapshot group;

    @OneToMany(mappedBy = "documentMatrix", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<DocumentSnapshot> documents;
}
