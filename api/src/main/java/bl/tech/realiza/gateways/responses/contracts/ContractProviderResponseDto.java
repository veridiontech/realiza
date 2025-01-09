package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contract.Activity;
import bl.tech.realiza.domains.contract.Requirement;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ContractProviderResponseDto {
    // provider
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String allocatedLimit;
    private Date startDate;
    private Date endDate;
    private List<Activity> activities;
    private List<Requirement> requirements;

    // supplier
    private String providerSupplier;

    // subcontractor
    private String contractReference;
    private String providerSubcontractor;
}
