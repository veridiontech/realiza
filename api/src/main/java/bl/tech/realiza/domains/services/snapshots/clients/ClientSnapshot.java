package bl.tech.realiza.domains.services.snapshots.clients;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClientSnapshot {
    @EmbeddedId
    private SnapshotId id;
    private String cnpj;
    private String corporateName; // razão social
    private String tradeName; // nome fantasia
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<BranchSnapshot> branches;
}
