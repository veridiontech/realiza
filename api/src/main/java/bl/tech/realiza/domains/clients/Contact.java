package bl.tech.realiza.domains.clients;

import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idContact;
    private String department;
    private String email;
    private String country;
    private String telephone;
    private Boolean mainContact;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "idBranch", nullable = false)
    private Branch branch;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "idProviderSupplier", nullable = false)
    private ProviderSupplier supplier;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "idProviderSubcontractor", nullable = false)
    private ProviderSubcontractor subcontractor;
}
