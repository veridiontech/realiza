package bl.tech.realiza.domains.users;

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
@DiscriminatorValue("PROVIDER")
public class UserProvider extends User{
    @ManyToOne
    private ProviderSupplier providerSupplier;
}
