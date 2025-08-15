package bl.tech.realiza.gateways.responses.dashboard;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

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
