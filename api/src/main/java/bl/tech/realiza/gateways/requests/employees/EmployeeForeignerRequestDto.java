package bl.tech.realiza.gateways.requests.employees;

import bl.tech.realiza.domains.employees.Employee;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class EmployeeForeignerRequestDto {
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
    private Employee.Situation situation;
    private String rneRnmFederalPoliceProtocol;
    private Date brazilEntryDate;
    private String passport;
    private String branch;
    private String supplier;
    private String subcontract;
    private List<String> idContracts;
}
