package bl.tech.realiza.domains.services.snapshots.documents;

import bl.tech.realiza.domains.contract.ContractDocument;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractDocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSnapshot;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "OWNER", discriminatorType = DiscriminatorType.STRING)
public abstract class DocumentSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Document.Status status = Document.Status.PENDENTE;
    private String type;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;
    private LocalDateTime versionDate;
    private LocalDateTime expirationDate;
    @Builder.Default
    private LocalDateTime documentDate = LocalDateTime.now();
    private LocalDateTime lastCheck;
    @Builder.Default
    private DocumentValidityEnum validity = DocumentValidityEnum.INDEFINITE;
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
    @JoinColumn(name = "documentId")
    @JsonBackReference
    private DocumentMatrixSnapshot documentMatrix;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ContractDocumentSnapshot> contractDocuments;
}
