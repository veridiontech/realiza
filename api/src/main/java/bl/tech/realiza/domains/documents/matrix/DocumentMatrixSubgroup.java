package bl.tech.realiza.domains.documents.matrix;

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
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = "subgroupName")
})
public class DocumentMatrixSubgroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentSubgroup;
    private String subgroupName;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "idDocumentGroup", nullable = false)
    private DocumentMatrixGroup group;

    @OneToMany(mappedBy = "subGroup", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DocumentMatrix> documentMatrix;
}
