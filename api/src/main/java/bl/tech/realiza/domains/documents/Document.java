package bl.tech.realiza.domains.documents;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
    @Builder.Default
    private Boolean isDocumentUnique = true;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Request request = Request.NONE;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idDocument")
    private DocumentMatrix documentMatrix;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------

    @ManyToMany(mappedBy = "documents")
    @JsonBackReference
    private List<Contract> contracts;

    @JsonIgnore
    @OneToMany(mappedBy = "idRecord", cascade = CascadeType.REMOVE)
    private List<AuditLogDocument> auditLogDocuments;

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
