package bl.tech.realiza.domains.documents.matrix;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DocumentMatrixSubgroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentSubgroup;
    private String subgroupName;
    @ManyToOne
    private DocumentMatrixGroup group;
}
