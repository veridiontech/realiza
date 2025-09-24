package bl.tech.realiza.gateways.requests.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DashboardFiltersRequestDto {
    private List<String> branchIds;
    private List<String> providerIds;
    private List<String> documentTypes;
    private List<String> responsibleIds;
    private List<ContractStatusEnum> activeContract;
    private List<Document.Status> statuses;
    private List<String> documentTitles;

    private List<String> providerCnpjs;
    private List<String> contractIds;
    private List<String> employeeIds;
    private List<String> employeeCpfs;
    private List<Employee.Situation> employeeSituations;
    private List<Boolean> documentDoesBlock;
    private List<DocumentValidityEnum> documentValidity;
    private List<LocalDate> documentUploadDate;
}
