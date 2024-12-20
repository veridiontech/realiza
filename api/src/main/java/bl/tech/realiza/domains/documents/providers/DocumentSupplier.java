package bl.tech.realiza.domains.documents.providers;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.providers.ProviderSupplier;
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
@DiscriminatorValue("SUPPLIER")
public class DocumentSupplier extends Document {
    @ManyToOne
    private ProviderSupplier providerSupplier;
}
