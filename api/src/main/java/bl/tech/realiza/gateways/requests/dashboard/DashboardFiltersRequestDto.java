package bl.tech.realiza.gateways.requests.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import lombok.Data;

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
    private List<String> employeeSituations;
    private List<String> documentDoesBlock;
    private List<String> documentValidity;
    private List<String> documentUploadDate;
}
