package bl.tech.realiza.domains.employees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("BRAZILIAN")
public class EmployeeBrazilian extends Employee {
    private String rg;
    private Date admission_date;
}