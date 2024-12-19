package bl.tech.realiza.domains.documents.providers;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.providers.ProviderSupplier;
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
@DiscriminatorValue("SUPPLIER")
public class DocumentSupplier extends Document {
    @ManyToOne
    private ProviderSupplier providerSupplier;
}
