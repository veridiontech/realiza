package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@Jacksonized
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DashboardFiltersResponse {
    private List<FilterList> branches;
    private List<FilterList> providers;
    private List<String> documentTypes;
    private List<FilterList> responsibles;
    private List<ContractStatusEnum> contractStatus;
    private List<Document.Status> statuses;
    private List<String> documentTitles;

    private List<String> providerCnpjs;
    private List<FilterList> contracts;
    private List<FilterList> employees;
    private List<String> employeeCpfs;
    private List<Employee.Situation> employeeSituations;
    private List<Boolean> documentDoesBlock;
    private List<DocumentValidityEnum> documentValidity;

    @Getter
    @Setter
    @Builder
    @Jacksonized
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class FilterList {
        private String id;
        private String name;
    }
}
