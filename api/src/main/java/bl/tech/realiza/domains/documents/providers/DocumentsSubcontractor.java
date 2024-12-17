package bl.tech.realiza.domains.documents.providers;

import bl.tech.realiza.domains.documents.Documents;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class DocumentsSubcontractor extends Documents {
    @ManyToOne
    private ProviderSubcontractor providerSubcontractor;
}
