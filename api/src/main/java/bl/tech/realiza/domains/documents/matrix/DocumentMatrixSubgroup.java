package bl.tech.realiza.domains.documents.matrix;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne(cascade = CascadeType.REMOVE)
    private DocumentMatrixGroup group;
}
