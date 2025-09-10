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
    @UniqueConstraint(columnNames = "groupName")
})
public class DocumentMatrixGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentGroup;
    private String groupName;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------

    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
    private List<DocumentMatrix> documents;

//    @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE)
//    private List<DocumentMatrixSubgroup> subgroups;
}
