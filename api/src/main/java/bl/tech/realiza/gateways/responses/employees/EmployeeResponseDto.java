package bl.tech.realiza.gateways.responses.employees;

import bl.tech.realiza.domains.documents.matrix.DocumentMatrix;
import bl.tech.realiza.domains.employees.Employee;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EmployeeResponseDto {
    // employee
    private String idEmployee;
    private String pis;
    private Employee.MaritalStatus maritalStatus;
    private Employee.ContractType contractType;
    private String cep;
    private String name;
    private String surname;
    private String profilePictureId;
    private byte[] profilePictureData;
    private String address;
    private String addressLine2;
    private String country;
    private String acronym;
    private String state;
    private Date birthDate;
    private String city;
    private String postalCode;
    private String gender;
    private String positionId;
    private String positionTitle;
    private String registration;
    private Double salary;
    private String cellphone;
    private String platformAccess;
    private String telephone;
    private String directory;
    private String email;
    private Employee.LevelOfEducation levelOfEducation;
    private String cboId;
    private String cboTitle;
    private String cboCode;
    private Employee.Situation situation;
    private List<ContractDto> contracts;
    private List<DocumentMatrix> documents;

    // brazilian
    private String rg;
    private Date admissionDate;
    private String cpf;

    // foreigner
    private String rneRnmFederalPoliceProtocol;
    private Date brazilEntryDate;
    private String passport;

    // client
    private String branch;

    // subcontractor
    private String subcontract;

    // supplier
    private String supplier;

    @Data
    @Builder
    public static class ContractDto {
        private String idContract;
        private String serviceName;
    }
}
