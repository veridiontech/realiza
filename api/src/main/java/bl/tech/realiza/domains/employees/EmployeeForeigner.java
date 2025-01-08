package bl.tech.realiza.domains.employees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Data
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
