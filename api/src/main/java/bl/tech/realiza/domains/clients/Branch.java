package bl.tech.realiza.domains.clients;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.user.UserClient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idBranch;
    private String name;
    private String cnpj;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "idClient", nullable = false)
    private Client client;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DocumentBranch> documentBranches;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ContractProviderSupplier> contracts;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Employee> employees;

    @ManyToMany(mappedBy = "branches", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<ProviderSupplier> providerSuppliers;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<UserClient> userClients;

}
