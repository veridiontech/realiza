package bl.tech.realiza.domains.documents.employee;

import bl.tech.realiza.domains.documents.Documents;
import bl.tech.realiza.domains.employees.Brazilian;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("BRAZILIAN")
public class DocumentsBrazilian extends Documents {
    @ManyToOne
    private Brazilian brazilian;
}
