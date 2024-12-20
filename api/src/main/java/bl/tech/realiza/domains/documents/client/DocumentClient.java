package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.clients.Client;
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
@DiscriminatorValue("CLIENT")
public class DocumentClient extends Document {
    @ManyToOne
    private Client client;
}
