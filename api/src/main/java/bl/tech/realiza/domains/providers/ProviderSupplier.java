package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.clients.Client;
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
public class ProviderSupplier extends Provider {
    @ManyToOne
    private Client client;
}
