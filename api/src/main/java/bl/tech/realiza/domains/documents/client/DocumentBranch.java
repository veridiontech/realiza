package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.documents.Document;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("BRANCH")
public class DocumentBranch extends Document {
    @ManyToOne
    private Branch branch;
}
