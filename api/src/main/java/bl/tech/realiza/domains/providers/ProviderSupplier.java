package bl.tech.realiza.domains.providers;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("PROVIDER")
public class ProviderSupplier extends Provider {
}
