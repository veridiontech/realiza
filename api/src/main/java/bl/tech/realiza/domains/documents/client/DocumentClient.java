package bl.tech.realiza.domains.documents.client;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.documents.Document;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("CLIENT")
public class DocumentClient extends Document {
    @ManyToOne(cascade = CascadeType.REMOVE)
    private Client client;
}
