package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.User;
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
@DiscriminatorColumn(name = "contract_type")
public abstract class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String contractReference;
    private String description;
    private String allocatedLimit;
    private ExpenseType expenseType;
    @OneToOne(cascade = CascadeType.REMOVE)
    private User responsible;
    private Date startDate;
    private Date endDate;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private Boolean deleteRequest = false;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_ACTIVITIES",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idActivity", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Activity> activities;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_REQUIREMENTS",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idRequirement", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Requirement> requirements;

    @ManyToMany(mappedBy = "contracts")
    private List<Employee> employees;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DocumentContract> documentContracts;

    public enum ExpenseType {
        CAPEX,
        OPEX
    }
}
