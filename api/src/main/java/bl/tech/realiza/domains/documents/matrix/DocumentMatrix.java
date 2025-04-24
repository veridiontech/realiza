package bl.tech.realiza.domains.documents.matrix;

import bl.tech.realiza.domains.documents.Document;
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
        @UniqueConstraint(columnNames = "name")
})
public class DocumentMatrix {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocument;
    private String name;
    @Builder.Default
    private Integer expirationDateAmount = 1;
    @Builder.Default
    private Unit expirationDateUnit = Unit.DAYS;
    private String type;
    private String doesBlock;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    public enum Unit {
        DAYS, WEEKS, MONTHS, YEARS
    }

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idDocumentSubgroup")
    private DocumentMatrixSubgroup subGroup;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToMany(mappedBy = "documentMatrix", cascade = CascadeType.REMOVE)
    private List<Document> documents;
}
