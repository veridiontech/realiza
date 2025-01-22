package bl.tech.realiza.domains.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "contract_type")
public abstract class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idEmployee;
    private String pis;
    private String maritalStatus;
    private String contractType;
    private String cep;
    private String name;
    private String surname;
    private String address;
    private String country;
    private String acronym;
    private String state;
    private Date birthDate;
    private String city;
    private String postalCode;
    private String gender;
    private String position;
    private String registration;
    private Double salary;
    private String cellphone;
    private String platformAccess;
    private String telephone;
    private String directory;
    private String email;
    private String levelOfEducation;
    private String cbo;
    private String situation;
    private LocalDateTime creationDate;
    @Builder.Default
    private Boolean isActive = true;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "client_id_client", referencedColumnName = "idClient")
    private Client client;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private ProviderSupplier supplier;
    @ManyToOne(cascade = CascadeType.REMOVE)
    private ProviderSubcontractor subcontract;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_CONTRACT",
            joinColumns = @JoinColumn(name = "idEmployee"),
            inverseJoinColumns = @JoinColumn(name = "idContract")
    )
    private List<Contract> contracts;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}
