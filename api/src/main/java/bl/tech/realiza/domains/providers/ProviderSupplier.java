package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.clients.Contact;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSubcontractor;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.UserProviderSubcontractor;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import bl.tech.realiza.domains.contract.Activity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DiscriminatorValue("SUPPLIER")
public class ProviderSupplier extends Provider {
    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToMany
    @JoinTable(
            name = "SUPPLIER_BRANCHS",
            joinColumns = @JoinColumn(name = "idProvider"),
            inverseJoinColumns = @JoinColumn(name = "idBranch")
    )
    private List<Branch> branches;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSupplier> contractsSupplier;

    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSubcontractor> contractsSubcontractor;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    private List<Employee> employees;

    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ProviderSubcontractor> providerSubcontracts;

    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<UserProviderSupplier> userProviderSuppliers;

    @OneToMany(mappedBy = "providerSupplier", cascade = CascadeType.REMOVE)
    private List<DocumentProviderSupplier> documentProviderSuppliers;
}
