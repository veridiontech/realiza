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
    @Builder.Default
    private Boolean lowLessThan8h = false;
    @Builder.Default
    private Boolean lowLessThan1m = false;
    @Builder.Default
    private Boolean lowLessThan6m = false;
    @Builder.Default
    private Boolean lowMoreThan6m = false;
    @Builder.Default
    private Boolean mediumLessThan1m = false;
    @Builder.Default
    private Boolean mediumLessThan6m = false;
    @Builder.Default
    private Boolean mediumMoreThan6m = false;
    @Builder.Default
    private Boolean highLessThan1m = false;
    @Builder.Default
    private Boolean highLessThan6m = false;
    @Builder.Default
    private Boolean highMoreThan6m = false;
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
        LOW_LESS_THAN_8H,
        LOW_LESS_THAN_1M,
        LOW_LESS_THAN_6M,
        LOW_MORE_THAN_6M,
        MEDIUM_LESS_THAN_1M,
        MEDIUM_LESS_THAN_6M,
        MEDIUM_MORE_THAN_6M,
        HIGH_LESS_THAN_1M,
        HIGH_LESS_THAN_6M,
        HIGH_MORE_THAN_6M
    }
}
