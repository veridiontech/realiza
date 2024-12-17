package bl.tech.realiza.domains.documents.providers;

import bl.tech.realiza.domains.documents.Documents;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUPPLIER")
public class DocumentsSupplier extends Documents {
    @ManyToOne
    private ProviderSupplier providerSupplier;
}
