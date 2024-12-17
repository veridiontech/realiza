package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.Client;
import bl.tech.realiza.domains.documents.Documents;
import jakarta.persistence.*;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIENT")
public class DocumentsClient extends Documents {
    @ManyToOne
    private Client client;
}
