package bl.tech.realiza.gateways.requests.employees;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class EmployeeBrazilianRequestDto {
    private String idEmployee;
    private String pis;
    private String maritalStatus;
    private String contract;
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
    private String rg;
    private Date admissionDate;
    private String client;
    private String supplier;
    private String subcontract;
    private Boolean isActive;
}
