package bl.tech.realiza.domains.documents.employee;

import bl.tech.realiza.domains.documents.Documents;
import bl.tech.realiza.domains.employees.EmployeeBrazilian;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("BRAZILIAN")
public class DocumentsBrazilian extends Documents {
    @ManyToOne
    private EmployeeBrazilian employeeBrazilian;
}
