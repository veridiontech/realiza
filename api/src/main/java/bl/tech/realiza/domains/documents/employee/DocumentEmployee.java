package bl.tech.realiza.domains.documents.employee;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("EMPLOYEE")
public class DocumentEmployee extends Document {
    @ManyToOne
    private Employee employee;
}
