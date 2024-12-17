package bl.tech.realiza.domains.employees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("FOREIGNER")
public class Foreigner extends Employee {
    private String rneRnmFederalPoliceProtocol;
    private Date brazilEntryDate;
    private String passport;
}
