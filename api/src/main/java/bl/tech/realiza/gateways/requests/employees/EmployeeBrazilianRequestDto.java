package bl.tech.realiza.gateways.requests.employees;

import bl.tech.realiza.domains.employees.Employee;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class EmployeeBrazilianRequestDto {
    private String pis;
    private Employee.MaritalStatus maritalStatus;
    private Employee.ContractType contractType;
    private String cep;
    private String name;
    private String surname;
    private String address;
    private String country;
    private String acronym;
    private String state;
    private Date birthDate;
    private String city;
    private String addressLine2;
    private String postalCode;
    private String gender;
    private String positionId;
    private String registration;
    private Double salary;
    private String cellphone;
    private String platformAccess;
    private String telephone;
    private String directory;
    private String email;
    private Employee.LevelOfEducation levelOfEducation;
    private String cboId;
    private Employee.Situation situation;
    private String rg;
    private String cpf;
    private Date admissionDate;
    private String branch;
    private String supplier;
    private String subcontract;
    private List<String> idContracts;
    private List<String> documents;
}
