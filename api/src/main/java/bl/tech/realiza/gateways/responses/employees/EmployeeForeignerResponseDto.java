package bl.tech.realiza.gateways.responses.employees;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmployeeForeignerResponseDto {
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
    private String rneRnmFederalPoliceProtocol;
    private Date brazilEntryDate;
    private String passport;
    private String client;
    private String supplier;
    private String subcontract;
}
