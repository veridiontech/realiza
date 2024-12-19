package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.Document;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIENT")
public class DocumentClient extends Document {
    @ManyToOne
    private Client client;
}
