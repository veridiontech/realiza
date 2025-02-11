package bl.tech.realiza.domains.documents;

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
    private String documentation;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime versionDate = LocalDateTime.now();
    private LocalDateTime expirationDate;
    @Enumerated(EnumType.STRING)
    private Risk risk;
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

    public enum Risk {
        LOW_LESS_8H,
        LOW_LESS_1M,
        LOW_LESS_6M,
        LOW_MORE_6M,
        MEDIUM_LESS_1M,
        MEDIUM_LESS_6M,
        MEDIUM_MORE_6M,
        HIGH_LESS_1M,
        HIGH_LESS_6M,
        HIGH_MORE_6M
    }

}
