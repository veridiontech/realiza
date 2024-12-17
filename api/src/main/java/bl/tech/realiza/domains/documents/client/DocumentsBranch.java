package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.Branch;
import bl.tech.realiza.domains.documents.Documents;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("BRANCH")
public class DocumentsBranch extends Documents {
    @ManyToOne
    private Branch branch;
}
