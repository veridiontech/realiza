package bl.tech.realiza.domains.documentation.employee;

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
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("FOREIGNER")
public class Foreigner extends Employee {
    private String rneRnmFederalPoliceProtocol;
    private Date brazilEntryDate;
    private String passport;
}
