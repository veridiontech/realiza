package bl.tech.realiza.domains.records.denied;

import bl.tech.realiza.domains.documents.client.DocumentClient;
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
public class DocumentRecordClient extends DocumentRecord {
    // alterar relacionamento
    @ManyToOne(cascade = CascadeType.REMOVE)
    private DocumentClient client;
}
