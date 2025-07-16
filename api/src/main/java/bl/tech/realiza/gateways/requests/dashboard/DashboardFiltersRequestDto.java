package bl.tech.realiza.gateways.requests.dashboard;

import bl.tech.realiza.domains.contract.Contract;
import bl.tech.realiza.domains.documents.Document;
import lombok.Data;

import java.util.List;

@Data
public class DashboardFiltersRequestDto {
    private List<String> branchIds;
    private List<String> providerIds;
    private List<String> documentTypes;
    private List<String> responsibleIds;
    private List<Contract.IsActive> activeContract;
    private List<Document.Status> statuses;
    private List<String> documentTitles;
}
