package bl.tech.realiza.domains.contract;

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
    @OneToOne(cascade = CascadeType.ALL)
    private User responsible;
    private Date startDate;
    private Date endDate;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private Boolean deleteRequest = false;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_ACTIVITIES",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idActivity")
    )
    private List<Activity> activities;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_REQUIREMENTS",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idRequirement")
    )
    private List<Requirement> requirements;

    public enum ExpenseType {
        CAPEX,
        OPEX
    }
}
