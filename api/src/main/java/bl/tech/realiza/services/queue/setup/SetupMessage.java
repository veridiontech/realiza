package bl.tech.realiza.services.queue.setup;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupMessage implements Serializable {
    private String type;
    private String clientId;
    private String branchId;
    private String contractSupplierId;
    private String contractSubcontractorId;
    private String contractId;
    private List<String> activityIds;
    private List<String> employeeIds;
    private Boolean profilesFromRepo;
}