package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import bl.tech.realiza.domains.services.snapshots.user.UserSnapshot;
import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class ContractSnapshot {
    @EmbeddedId
    @AttributeOverrides({
            @AttributeOverride(name = "id", column = @Column(name = "contract_id")),
            @AttributeOverride(name = "frequency", column = @Column(name = "contract_frequency")),
            @AttributeOverride(name = "snapshotDate", column = @Column(name = "contract_snapshot_date"))
    })
    private SnapshotId id;
    private String reference;
    private String serviceType;
    @Builder.Default
    private ContractStatusEnum status = ContractStatusEnum.PENDING;
    private Date start;
    private Date finish;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "userId", referencedColumnName = "id"),
            @JoinColumn(name = "userFrequency", referencedColumnName = "frequency"),
            @JoinColumn(name = "userSnapshotDate", referencedColumnName = "snapshotDate")
    })
    @JsonManagedReference
    private UserSnapshot responsible;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<ContractDocumentSnapshot> contractDocuments;

    @ManyToMany(mappedBy = "contracts")
    @JsonManagedReference
    private List<EmployeeSnapshot> employees;
}
