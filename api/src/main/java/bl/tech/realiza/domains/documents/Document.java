package bl.tech.realiza.domains.documents;

import bl.tech.realiza.domains.auditLogs.document.AuditLogDocument;
import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import bl.tech.realiza.domains.services.FileDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

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
    @Builder.Default
    private Status status = Status.PENDENTE;
    private String type;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime versionDate;
    private LocalDateTime expirationDate;
    @Builder.Default
    private Integer expirationDateAmount = 1;
    @Builder.Default
    private DocumentMatrix.DayUnitEnum expirationDateUnit = DocumentMatrix.DayUnitEnum.MONTHS;
    @Builder.Default
    private LocalDateTime documentDate = LocalDateTime.now();
    private LocalDateTime lastCheck;
    @Builder.Default
    private DocumentValidityEnum validity = DocumentValidityEnum.INDEFINITE;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Boolean adherent = false;
    @Builder.Default
    private Boolean conforming = false;
    @Builder.Default
    private Boolean doesBlock = true;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idDocument")
    private DocumentMatrix documentMatrix;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FileDocument> document;
    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContractDocument> contractDocuments;

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
        VENCIDO,
        ISENCAO_PENDENTE,
        ISENTO
    }

    public String getWeeklyTitle() {
        LocalDate date = LocalDate.now();
        int week = date.get(WeekFields.of(Locale.getDefault()).weekOfMonth());
        String month = date.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        return String.format("%s - Semana %s de %s", this.title != null ? this.title : "", week, month).trim();
    }

    public String getMonthlyTitle() {
        LocalDate date = LocalDate.now();
        String month = date.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        return String.format("%s - %s", this.title != null ? this.title : "", month).trim();
    }

    public String getAnnualTitle() {
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        return String.format("%s - %s", this.title != null ? this.title : "", year).trim();
    }
}
