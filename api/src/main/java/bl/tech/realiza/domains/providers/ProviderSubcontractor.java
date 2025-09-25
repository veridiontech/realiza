package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.clients.Contact;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class ProviderSubcontractor extends Provider {
    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idProviderSupplier")
    private ProviderSupplier providerSupplier;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.REMOVE)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "providerSubcontractor")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSubcontractor> contractsSubcontractor;

    @OneToMany(mappedBy = "subcontract", cascade = CascadeType.REMOVE)
    private List<Employee> employees;

    @OneToMany(mappedBy = "providerSubcontractor")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<UserProviderSubcontractor> userProviderSubcontractors;

    @OneToMany(mappedBy = "providerSubcontractor", cascade = CascadeType.REMOVE)
    private List<DocumentProviderSubcontractor> documentProviderSubcontractors;
}
