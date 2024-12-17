package bl.tech.realiza.domains.documents.employee;

import bl.tech.realiza.domains.documents.Documents;
import bl.tech.realiza.domains.employees.EmployeeForeigner;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("FOREIGNER")
public class DocumentsForeigner extends Documents {
    @ManyToOne
    private EmployeeForeigner employeeForeigner;
}
