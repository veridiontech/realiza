package bl.tech.realiza.domains.providers;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contract_type")
public abstract class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id_provider;
    private String cnpj;
}
