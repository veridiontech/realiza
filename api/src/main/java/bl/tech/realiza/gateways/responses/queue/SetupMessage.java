package bl.tech.realiza.gateways.responses.queue;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetupMessage implements Serializable {
    private String type;
    private String clientId;
    private String branchId;
    private List<String> branchIds;
    private String contractSupplierId;
    private String contractSubcontractorId;
    private String contractId;
    private List<String> activityIds;
    private List<String> employeeIds;
    private String activityId;
    private String serviceTypeBranchId;
    private String documentId;
    private String title;
    private Activity.Risk activityRisk;
    private ServiceType.Risk serviceTypeRisk;
}