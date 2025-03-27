package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.services.ItemManagement;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contract_type")
public abstract class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idProvider;
    private String cnpj; //
    private String tradeName;
    private String corporateName; //
    private String logo;
    private String email; //
    private String telephone; //
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    @Builder.Default
    private Boolean isActive = false;
    @Builder.Default
    private Boolean denied = false;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToOne(mappedBy = "newProvider", cascade = CascadeType.REMOVE)
    private ItemManagement newProviderSolicitation;

    public enum Company {
        CLIENT,
        SUPPLIER,
        SUBCONTRACTOR
    }
}
