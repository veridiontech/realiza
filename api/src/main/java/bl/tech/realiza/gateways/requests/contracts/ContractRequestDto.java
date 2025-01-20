package bl.tech.realiza.gateways.requests.contracts;

import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractRequestDto {
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String allocatedLimit;
    private Date startDate;
    private Date endDate;
    private String contractReference;
    private List<String> activities;
    private List<String> requirements;
    private Boolean isActive;

    // supplier
    private String providerSupplier;

    // subcontractor
    private String providerSubcontractor;
}
