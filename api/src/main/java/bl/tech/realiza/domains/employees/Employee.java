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
    @Builder.Default
    private Situation situation = Situation.DESALOCADO;
    @Builder.Default
    private Boolean deleteRequest = false;
    @Builder.Default
    private LocalDateTime creationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "idBranch", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "idProviderSupplier", nullable = false)
    private ProviderSupplier supplier;

    @ManyToOne
    @JoinColumn(name = "idProviderSubcontractor", nullable = false)
    private ProviderSubcontractor subcontract;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_CONTRACT",
            joinColumns = @JoinColumn(name = "idEmployee"),
            inverseJoinColumns = @JoinColumn(name = "idContract", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<Contract> contracts;

    @ManyToMany
    @JoinTable(
            name = "EMPLOYEE_DOCUMENT_MATRIX",
            joinColumns = @JoinColumn(name = "idEmployee"),
            inverseJoinColumns = @JoinColumn(name = "idDocument", foreignKey = @ForeignKey(value = ConstraintMode.CONSTRAINT))
    )
    private List<DocumentMatrix> documents;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.REMOVE, orphanRemoval = true)
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
