package bl.tech.realiza.domains.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
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
public class DocumentMatrixSubgroupSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @ManyToOne
    @JoinColumn(name = "documentGroupId")
    @JsonManagedReference
    private DocumentMatrixGroupSnapshot group;

    @OneToMany(mappedBy = "subGroup", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<DocumentMatrixSnapshot> documentsMatrix;
}
