package bl.tech.realiza.domains.clients;

import bl.tech.realiza.domains.auditLogs.enterprise.AuditLogBranch;
import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.ContractProviderSupplier;
import bl.tech.realiza.domains.contract.Requirement;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import bl.tech.realiza.domains.ultragaz.Center;
import bl.tech.realiza.domains.user.Profile;
import bl.tech.realiza.domains.user.User;
import bl.tech.realiza.domains.user.UserClient;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString(exclude = {"client"})
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Branch {

    /*
    unidade -> branch -> 4º nível
    núcleo -> center -> 3º nível
    mercado -> market -> 2º nível
    board -> diretoria -> 1º nível
     */
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
    private Boolean base = false;
    @Builder.Default
    private Boolean isActive = true;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idClient")
    @JsonBackReference
    private Client client;

    @ManyToMany
    @JoinTable(
            name = "CENTER_BRANCH",
            joinColumns = @JoinColumn(name = "idBranch"),
            inverseJoinColumns = @JoinColumn(name = "idCenter",
                    foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Center> center;

    @ManyToMany
    @JoinTable(
            name = "CONTRACT_REQUIREMENTS",
            joinColumns = @JoinColumn(name = "idContract"),
            inverseJoinColumns = @JoinColumn(name = "idRequirement", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Requirement> requirements;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @JsonIgnore
    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE)
    private List<Contact> contacts;

    @JsonIgnore
    @OneToMany(mappedBy = "branch", cascade = CascadeType.REMOVE)
    private List<DocumentBranch> documentBranches;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<ContractProviderSupplier> contracts;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Employee> employees;

    @JsonIgnore
    @ManyToMany(mappedBy = "branches")
    private List<ProviderSupplier> providerSuppliers;

    @JsonIgnore
    @ManyToMany(mappedBy = "branchesAccess")
    private List<User> userAccess;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<UserClient> userClients;

    @JsonIgnore
    @OneToMany(mappedBy = "branch")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private List<Activity> activities;

    @JsonIgnore
    @OneToMany(mappedBy = "idRecord", cascade = CascadeType.REMOVE)
    private List<AuditLogBranch> auditLogBranches;
}