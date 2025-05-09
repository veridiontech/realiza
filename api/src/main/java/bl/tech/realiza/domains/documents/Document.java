package bl.tech.realiza.domains.documents;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "documentation_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idDocumentation;
    private String title;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String type;
    private String documentation;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime versionDate;
    private LocalDateTime expirationDate;
    @Builder.Default
    private Integer expirationDateAmount = 1;
    @Builder.Default
    private DocumentMatrix.Unit expirationDateUnit = DocumentMatrix.Unit.MONTHS;
    @Builder.Default
    private Boolean isActive = true;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Request request = Request.NONE;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idDocument")
    private DocumentMatrix documentMatrix;

    public enum Status {
        PENDENTE,
        EM_ANALISE,
        REPROVADO,
        APROVADO,
        REPROVADO_IA,
        APROVADO_IA,
        VENCIDO
    }

    public enum Request {
        NONE,
        DELETE,
        ADD
    }
}
