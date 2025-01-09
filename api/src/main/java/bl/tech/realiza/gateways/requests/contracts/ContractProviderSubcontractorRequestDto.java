package bl.tech.realiza.gateways.requests.contracts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractProviderSubcontractorRequestDto {
    private String idContract;
    @NotEmpty
    private String serviceType;
    @NotEmpty
    private String serviceDuration;
    @NotEmpty
    private String serviceName;
    @NotEmpty
    private String description;
    @NotEmpty
    private String allocatedLimit;
    @NotNull
    private Date startDate;
    @NotNull
    private Date endDate;
    @NotEmpty
    private String contractReference;
    @NotEmpty
    private String providerSubcontractor;
    @NotEmpty
    private List<String> activities;
    @NotEmpty
    private List<String> requirements;
}
