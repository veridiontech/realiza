package bl.tech.realiza.domains.employees;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@DiscriminatorValue("BRAZILIAN")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "cpf"))
public class EmployeeBrazilian extends Employee {
    private Date admissionDate;
    private String cpf;
}
