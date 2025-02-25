package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.clients.Contact;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.UserClient;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUBCONTRACTOR")
public class ProviderSubcontractor extends Provider {
    @ManyToOne
    @JoinColumn(name = "idProviderSupplier", nullable = false)
    private ProviderSupplier providerSupplier;

    @OneToMany(mappedBy = "subcontractor", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "providerSubcontractor", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ContractProviderSubcontractor> contractsSubcontractor;

    @OneToMany(mappedBy = "subcontract", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Employee> employees;

    @OneToMany(mappedBy = "providerSubcontractor", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserProviderSubcontractor> userProviderSubcontractors;

    @OneToMany(mappedBy = "providerSubcontractor", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DocumentProviderSubcontractor> documentProviderSubcontractors;
}
