package bl.tech.realiza.domains.users;

import bl.tech.realiza.domains.providers.Provider;
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
@DiscriminatorValue("PROVIDER")
public class UserProvider extends User{
    @ManyToOne
    private ProviderSupplier providerSupplier;
}
