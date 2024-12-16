package bl.tech.realiza.domains.documentation.employee;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
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
    private String salary;
    private String cellphone;
    private String platform_access;
    private String telephone;
    private String directory;
    private String email;
    private String level_of_education;
    private String cbo;
}
