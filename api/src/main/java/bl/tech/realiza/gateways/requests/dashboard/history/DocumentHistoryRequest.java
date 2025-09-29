package bl.tech.realiza.gateways.requests.dashboard.history;

import bl.tech.realiza.domains.documents.Document;
import bl.tech.realiza.domains.employees.Employee;
import bl.tech.realiza.domains.enums.ContractStatusEnum;
import bl.tech.realiza.domains.enums.DocumentValidityEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.time.Month;
import java.time.Year;
import java.util.List;

@Getter
@Setter
@Builder
@Jacksonized
public class DocumentHistoryRequest {
    @NotNull
    private Month startMonth;
    @NotNull
    private Year startYear;
    @NotNull
    private Month endMonth;
    @NotNull
    private Year endYear;

    private List<String> branchIds;
    private List<String> providerIds;
    private List<String> providerCnpjs;
    private List<String> documentTypes;
}
