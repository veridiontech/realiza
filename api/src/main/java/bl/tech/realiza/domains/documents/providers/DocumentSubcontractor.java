package bl.tech.realiza.domains.documents.providers;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class DocumentSubcontractor extends Document {
    @ManyToOne
    private ProviderSubcontractor providerSubcontractor;
}
