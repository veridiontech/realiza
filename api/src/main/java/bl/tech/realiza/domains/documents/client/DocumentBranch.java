package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("BRANCH")
public class DocumentBranch extends Document {
    @ManyToOne
    private Branch branch;
}
