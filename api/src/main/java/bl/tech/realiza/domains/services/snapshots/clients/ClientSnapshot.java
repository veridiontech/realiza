package bl.tech.realiza.domains.services.snapshots.clients;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ClientSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String cnpj;
    private String corporateName; // razão social
    private String tradeName; // nome fantasia
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE)
    @JsonBackReference
    private List<BranchSnapshot> branches;
}
