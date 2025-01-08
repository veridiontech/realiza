package bl.tech.realiza.domains.employees;

import bl.tech.realiza.domains.clients.Client;
import bl.tech.realiza.domains.providers.ProviderSubcontractor;
import bl.tech.realiza.domains.providers.ProviderSupplier;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.sql.Date;

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
    private String id_employee;
    private String pis;
    private String marital_status;
    private String contract;
    private String cep;
    private String name;
    private String surname;
    private String address;
    private String country;
    private String acronym;
    private String state;
    private Date birth_date;
    private String city;
    private String postal_code;
    private String gender;
    private String position;
    private String registration;
    private Double salary;
    private String cellphone;
    private String platform_access;
    private String telephone;
    private String directory;
    private String email;
    private String level_of_education;
    private String cbo;

    @ManyToOne
    private Client client;
    @ManyToOne
    private ProviderSupplier supplier;
    @ManyToOne
    private ProviderSubcontractor subcontract;
}
