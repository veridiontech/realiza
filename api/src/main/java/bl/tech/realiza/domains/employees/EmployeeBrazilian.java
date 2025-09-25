package bl.tech.realiza.domains.employees;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.br.CPF;

import java.sql.Date;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("BRAZILIAN")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "cpf"))
public class EmployeeBrazilian extends Employee {
    private Date admissionDate;
    @CPF
    private String cpf;

    public void setCpf(String cpf) {
        this.cpf = cpf == null ? null : cpf.replaceAll("\\D", "");
    }

    @Transient
    public String getCpfFormatted() {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.replaceFirst("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
