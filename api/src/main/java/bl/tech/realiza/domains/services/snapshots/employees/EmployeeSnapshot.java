package bl.tech.realiza.domains.services.snapshots.employees;

import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.employee.DocumentEmployeeSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmployeeSnapshot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String surname;
    private String position;
    private String cbo;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private SnapshotFrequencyEnum frequency;

    @ManyToOne
    @JoinColumn(name = "supplierId")
    @JsonBackReference
    private ProviderSupplierSnapshot supplier;

    @ManyToOne
    @JoinColumn(name = "subcontractorId")
    @JsonBackReference
    private ProviderSubcontractorSnapshot subcontractor;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_CONTRACT_SNAPSHOT",
            joinColumns = @JoinColumn(name = "employeeId"),
            inverseJoinColumns = @JoinColumn(name = "contractId", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    @JsonBackReference
    private List<ContractSnapshot> contracts;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<DocumentEmployeeSnapshot> documentsEmployee;

    public String getFullName() {
        return String.format("%s %s", this.name != null ? this.name : "", this.surname != null ? this.surname : "").trim();
    }
}
