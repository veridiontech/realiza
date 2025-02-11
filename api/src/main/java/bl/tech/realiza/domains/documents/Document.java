package bl.tech.realiza.domains.documents;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private String documentation;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime versionDate = LocalDateTime.now();
    private LocalDateTime expirationDate;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Request request = Request.NONE;

    @ManyToOne(cascade = CascadeType.REMOVE)
    private DocumentMatrix documentMatrix;

    public enum Status {
        PENDENTE,
        EM_ANALISE,
        REPROVADO,
        APROVADO,
        VENCIDO
    }

    public enum Request {
        NONE,
        DELETE,
        ADD
    }
}
