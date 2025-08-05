package bl.tech.realiza.domains.services.snapshots.contract;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.employees.EmployeeSnapshot;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.services.snapshots.user.UserSnapshot;
import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type")
public abstract class ContractSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String reference;
    private String serviceType;
    @Builder.Default
    private ContractStatusEnum status = ContractStatusEnum.PENDING;
    private Date start;
    private Date finish;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonManagedReference
    private UserSnapshot responsible;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<ContractDocumentSnapshot> contractDocuments;

    @ManyToMany(mappedBy = "contracts")
    @JsonManagedReference
    private List<EmployeeSnapshot> employees;
}
