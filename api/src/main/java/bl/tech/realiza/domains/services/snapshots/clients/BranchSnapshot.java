package bl.tech.realiza.domains.services.snapshots.clients;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractProviderSupplierSnapshot;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BranchSnapshot {
    @EmbeddedId
    private SnapshotId id;
    private String tradeName;
    private String cnpj;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "clientId", referencedColumnName = "id"),
            @JoinColumn(name = "clientFrequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "clientSnapshotDate", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private ClientSnapshot client;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ProviderSupplierSnapshot> suppliers;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractProviderSupplierSnapshot> contracts;
}