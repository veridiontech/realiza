package bl.tech.realiza.domains.services.snapshots.documents.matrix;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
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
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String type;
    @Builder.Default
    private Boolean unique = true;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @ManyToOne
    @JoinColumn(name = "documentSubgroupId")
    @JsonManagedReference
    private DocumentMatrixSubgroupSnapshot subGroup;

    @OneToMany(mappedBy = "documentMatrix", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<DocumentSnapshot> documents;
}
