package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static bl.tech.realiza.domains.documents.Document.Status.PENDENTE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContractDocumentSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;
    @Builder.Default
    private Document.Status status = PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractId")
    @JsonBackReference
    private ContractSnapshot contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "documentId")
    @JsonBackReference
    private DocumentSnapshot document;
}
