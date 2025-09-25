package bl.tech.realiza.domains.services.snapshots.documents;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.services.snapshots.contract.ContractDocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.matrix.DocumentMatrixSnapshot;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "OWNER", discriminatorType = DiscriminatorType.STRING)
public abstract class DocumentSnapshot {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "document_id")),
            @AttributeOverride(name = "frequency", column = @Column(name = "document_frequency")),
            @AttributeOverride(name = "snapshotDate", column = @Column(name = "document_snapshot_date"))
    })
    private SnapshotId id;
    private String title;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Document.Status status = Document.Status.PENDENTE;
    private String type;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
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
    @JoinColumns({
            @JoinColumn(name = "documentMatrixId", referencedColumnName = "id"),
            @JoinColumn(name = "documentMatrixFrequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "documentMatrixSnapshotDate", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private DocumentMatrixSnapshot documentMatrix;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractDocumentSnapshot> contractDocuments;
}
