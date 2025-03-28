package bl.tech.realiza.domains.contract;

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
    @Builder.Default
    private ExpenseType expenseType = ExpenseType.NENHUM;
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
    @JoinColumn(name = "idActivity")
    private Activity activity;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_REQUIREMENTS",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idRequirement", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Requirement> requirements;

    @ManyToMany(mappedBy = "contracts")
    private List<Employee> employees;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.REMOVE)
    private List<DocumentContract> documentContracts;

    public enum ExpenseType {
        CAPEX,
        OPEX,
        NENHUM
    }

    public enum IsActive {
        ATIVADO,
        PENDENTE,
        NEGADO
    }
}
