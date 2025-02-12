package bl.tech.realiza.domains.user;

import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
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
@DiscriminatorValue("SUPPLIER")
public class UserProviderSupplier extends User {
    @ManyToOne(cascade = CascadeType.ALL)
    private ProviderSupplier providerSupplier;
}
