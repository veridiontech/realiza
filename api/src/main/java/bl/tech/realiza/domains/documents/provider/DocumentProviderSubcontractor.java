package bl.tech.realiza.domains.documents.provider;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import jakarta.persistence.*;
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
@DiscriminatorValue("SUBCONTRACTOR")
public class DocumentProviderSubcontractor extends Document {
    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idProviderSupplier")
    private ProviderSubcontractor providerSubcontractor;
}
