package bl.tech.realiza.domains.documents.employee;

import bl.tech.realiza.domains.documents.Documents;
import bl.tech.realiza.domains.employees.Foreigner;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("FOREIGNER")
public class DocumentsForeigner extends Documents {
    @ManyToOne
    private Foreigner foreigner;
}
