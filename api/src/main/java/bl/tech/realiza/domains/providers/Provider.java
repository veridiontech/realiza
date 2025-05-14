package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogProvider;
import bl.tech.realiza.domains.services.ItemManagement;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

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
    private String cnpj;
    private String tradeName; // nome fantasia
    private String corporateName; // razão social
    private String logo;
    private String email;
    private String telephone;
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
    @JsonIgnore
    @OneToOne(mappedBy = "newProvider", cascade = CascadeType.REMOVE)
    private ItemManagement newProviderSolicitation;

    @JsonIgnore
    @OneToMany(mappedBy = "idRecord", cascade = CascadeType.REMOVE)
    private List<AuditLogProvider> auditLogProviders;

    public enum Company {
        CLIENT,
        SUPPLIER,
        SUBCONTRACTOR
    }
}
