package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.auditLogs.contract.AuditLogContract;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceTypeBranch;
import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.contract.DocumentContract;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@DiscriminatorColumn(name = "contract_type")
public abstract class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idContract;
    private String serviceDuration;
    private String serviceName;
    private String contractReference;
    private String description;
    private String allocatedLimit;
    @Builder.Default
    private Boolean finished = false;
    @Builder.Default
    private ExpenseType expenseType = ExpenseType.NENHUM;
    @Builder.Default
    private Boolean labor = false;
    @Builder.Default
    private Boolean hse = false;
    private Date dateStart;
    private Date endDate;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @Builder.Default
    private IsActive isActive = IsActive.PENDENTE;
    @Builder.Default
    private Boolean deleteRequest = false;

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------

    @ManyToOne
    @JoinColumn(name = "idUser")
    private User responsible;

    @ManyToOne
    @JoinColumn(name = "idServiceType")
    private ServiceTypeBranch serviceTypeBranch;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_ACTIVITY",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idActivity", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    @JsonIgnore
    private List<Activity> activities;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContractDocument> contractDocuments;

    @ManyToMany(mappedBy = "contracts")
    private List<Employee> employees;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    private List<DocumentContract> documentContracts;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------


    @JsonIgnore
    @ManyToMany(mappedBy = "contractsAccess")
    private List<User> userAccess;

    @JsonIgnore
    @OneToMany(mappedBy = "idRecord", cascade = CascadeType.REMOVE)
    private List<AuditLogContract> auditLogContracts;

    public enum ExpenseType {
        CAPEX,
        OPEX,
        NENHUM
    }

    public enum IsActive {
        ATIVADO,
        PENDENTE,
        NEGADO,
        SUSPENSO
    }
}
