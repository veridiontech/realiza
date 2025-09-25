package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.documents.DocumentSnapshot;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static bl.tech.realiza.domains.documents.Document.Status.PENDENTE;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ContractDocumentSnapshot {
    @EmbeddedId
    private SnapshotId id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private Document.Status status = PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "contract_id", referencedColumnName = "contract_id"),
            @JoinColumn(name = "contract_frequency", referencedColumnName = "contract_frequency"),
            @JoinColumn(name = "contract_snapshot_date", referencedColumnName = "contract_snapshot_date")
    })
    @JsonManagedReference
    private ContractSnapshot contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "document_id", referencedColumnName = "document_id"),
            @JoinColumn(name = "document_frequency", referencedColumnName = "document_frequency"),
            @JoinColumn(name = "document_snapshot_date", referencedColumnName = "document_snapshot_date")
    })
    @JsonManagedReference
    private DocumentSnapshot document;
}
