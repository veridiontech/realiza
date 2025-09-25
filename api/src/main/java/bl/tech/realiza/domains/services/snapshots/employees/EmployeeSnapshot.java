package bl.tech.realiza.domains.services.snapshots.employees;

import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.SnapshotFrequencyEnum;
import bl.tech.realiza.domains.services.snapshots.contract.ContractSnapshot;
import bl.tech.realiza.domains.services.snapshots.documents.employee.DocumentEmployeeSnapshot;
import bl.tech.realiza.domains.services.snapshots.ids.SnapshotId;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSubcontractorSnapshot;
import bl.tech.realiza.domains.services.snapshots.providers.ProviderSupplierSnapshot;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmployeeSnapshot {
    @EmbeddedId
    private SnapshotId id;
    private String name;
    private String surname;
    private String position;
    private String cbo;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    private Employee.Situation situation;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "supplierId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "supplierFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "supplierSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    @JsonManagedReference
    private ProviderSupplierSnapshot supplier;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "subcontractorId", referencedColumnName = "provider_id"),
            @JoinColumn(name = "subcontractorFrequency", referencedColumnName = "provider_frequency"),
            @JoinColumn(name = "subcontractorSnapshotDate", referencedColumnName = "provider_snapshot_date")
    })
    @JsonManagedReference
    private ProviderSubcontractorSnapshot subcontractor;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_CONTRACT_SNAPSHOT",
            joinColumns = {
                    @JoinColumn(name = "employeeId", referencedColumnName = "id"),
                    @JoinColumn(name = "employeeFrequency", referencedColumnName = "frequency"),
                    @JoinColumn(name = "employeeSnapshotDate", referencedColumnName = "snapshotDate")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "contract_id", referencedColumnName = "contract_id"),
                    @JoinColumn(name = "contract_frequency", referencedColumnName = "contract_frequency"),
                    @JoinColumn(name = "contract_snapshot_date", referencedColumnName = "contract_snapshot_date")
            }
    )
    @JsonBackReference
    private List<ContractSnapshot> contracts;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<DocumentEmployeeSnapshot> documentsEmployee;

    public String getFullName() {
        return String.format("%s %s", this.name != null ? this.name : "", this.surname != null ? this.surname : "").trim();
    }
}
