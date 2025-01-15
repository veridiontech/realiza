package bl.tech.realiza.domains.contract;

import bl.tech.realiza.domains.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
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
    private String description;
    private String allocatedLimit;
    @OneToOne(cascade = CascadeType.ALL)
    private User responsible;
    private Date startDate;
    private Date endDate;
    private Boolean isActive = true;

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
}
