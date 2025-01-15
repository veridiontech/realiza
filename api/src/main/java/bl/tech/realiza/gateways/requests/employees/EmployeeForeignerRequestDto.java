package bl.tech.realiza.gateways.requests.employees;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;

@Data
public class EmployeeForeignerRequestDto {
    private String idEmployee;
    @NotEmpty
    private String pis;
    @NotEmpty
    private String maritalStatus;
    @NotEmpty
    private String contract;
    @NotEmpty
    private String cep;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    @NotEmpty
    private String address;
    @NotEmpty
    private String country;
    @NotEmpty
    private String acronym;
    @NotEmpty
    private String state;
    @NotNull
    private Date birthDate;
    @NotEmpty
    private String city;
    @NotEmpty
    private String postalCode;
    @NotEmpty
    private String gender;
    @NotEmpty
    private String position;
    @NotEmpty
    private String registration;
    @NotNull
    private Double salary;
    @NotEmpty
    private String cellphone;
    @NotEmpty
    private String platformAccess;
    @NotEmpty
    private String telephone;
    @NotEmpty
    private String directory;
    @NotEmpty
    private String email;
    @NotEmpty
    private String levelOfEducation;
    @NotEmpty
    private String cbo;
    @NotEmpty
    private String rneRnmFederalPoliceProtocol;
    @NotNull
    private Date brazilEntryDate;
    @NotEmpty
    private String passport;
    @NotEmpty
    private String client;
    @NotEmpty
    private String supplier;
    @NotEmpty
    private String subcontract;
    private Boolean isActive;
}
