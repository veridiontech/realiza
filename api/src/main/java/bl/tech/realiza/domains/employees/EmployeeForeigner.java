package bl.tech.realiza.domains.employees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("FOREIGNER")
public class EmployeeForeigner extends Employee {
    private String rneRnmFederalPoliceProtocol;
    private Date brazilEntryDate;
    private String passport;
}
