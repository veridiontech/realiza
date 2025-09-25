package bl.tech.realiza.domains.providers;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.clients.Contact;
import bl.tech.realiza.domains.contract.ContractProviderSubcontractor;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.provider.DocumentProviderSupplier;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.user.UserProviderSupplier;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
    @JsonIgnore
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    private List<Contact> contacts;

    @JsonIgnore
    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSupplier> contractsSupplier;

    @JsonIgnore
    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSubcontractor> contractsSubcontractor;

    @JsonIgnore
    @OneToMany(mappedBy = "supplier", cascade = CascadeType.REMOVE)
    private List<Employee> employees;

    @JsonIgnore
    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ProviderSubcontractor> providerSubcontracts;

    @JsonIgnore
    @OneToMany(mappedBy = "providerSupplier")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<UserProviderSupplier> userProviderSuppliers;

    @JsonIgnore
    @OneToMany(mappedBy = "providerSupplier", cascade = CascadeType.REMOVE)
    private List<DocumentProviderSupplier> documentProviderSuppliers;
}
