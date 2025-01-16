package bl.tech.realiza.gateways.requests.contracts;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Date;
import java.util.List;

@Data
public class ContractProviderSupplierRequestDto {
    private String idContract;
    private String serviceType;
    private String serviceDuration;
    private String serviceName;
    private String description;
    private String allocatedLimit;
    private Date startDate;
    private Date endDate;
    private String providerSupplier;
    private List<String> activities;
    private List<String> requirements;
    private Boolean isActive;
}
