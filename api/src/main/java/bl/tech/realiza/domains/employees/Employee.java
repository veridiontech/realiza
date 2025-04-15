package bl.tech.realiza.domains.employees;

import bl.tech.realiza.domains.clients.Branch;
import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.client.DocumentBranch;
import bl.tech.realiza.domains.documents.employee.DocumentEmployee;
import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
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
@DiscriminatorColumn(name = "employee_type")
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
    private String profilePicture;
    private String acronym;
    private Date birthDate;
    private String postalCode;
    private String address;
    private String city;
    private String state;
    private String country;
    private String gender;
    private String position;
    private String registration;
    private Double salary;
    private String cellphone;
    private String platformAccess;
    private String telephone;
    private String directory;
    private String levelOfEducation;
    private String cbo;
    @Builder.Default
    private Situation situation = Situation.DESALOCADO;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    // -------------------------------
    // Relacionamentos INERENTES
    // -------------------------------
    @ManyToOne
    @JoinColumn(name = "idBranch")
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "idProviderSupplier")
    private ProviderSupplier supplier;

    @ManyToOne
    @JoinColumn(name = "idProviderSubcontractor")
    private ProviderSubcontractor subcontract;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_CONTRACT",
            joinColumns = @JoinColumn(name = "idEmployee"),
            inverseJoinColumns = @JoinColumn(name = "idContract", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Contract> contracts;

    // -------------------------------
    // Relacionamentos CONTRATUAIS
    // -------------------------------
    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE)
    private List<DocumentEmployee> documentEmployees;

    public enum Situation {
        ALOCADO,
        DESALOCADO,
        DEMITIDO,
        AFASTADO,
        LICENCA_MATERNIDADE,
        LICENCA_MEDICA,
        LICENCA_MILITAR,
        FERIAS,
        ALISTAMENTO_MILITAR,
        APOSENTADORIA_POR_INVALIDEZ
    }
}
