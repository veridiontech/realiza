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
    private Unit expirationDateUnit = Unit.MONTHS;
    private String type;
    @Builder.Default
    private Boolean doesBlock = true;
    // se um documento é unico entre todos os contratos = true, caso seja por contrato = false
    @Builder.Default
    private Boolean isDocumentUnique = true;
    // TODO documento tem competencia (surge semanalmente? mensalmente? anualmente?)
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    public enum Unit {
        DAYS,
        WEEKS,
        MONTHS,
        YEARS
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
