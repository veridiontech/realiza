package bl.tech.realiza.domains.contract;

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
    private String id_contract;
    private String service_type;
    private String service_duration;
    private String service_name;
    private String description;
    private String allocated_limit;
    private Date start_date;
    private Date end_date;

    @ManyToMany
    private List<Activity> activities;
    @ManyToMany
    private List<Requirement> requirements;
}
