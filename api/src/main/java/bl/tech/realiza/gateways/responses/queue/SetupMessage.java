package bl.tech.realiza.gateways.responses.queue;

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
    private String contractSupplierId;
    private String contractSubcontractorId;
    private String contractId;
    private List<String> activityIds;
    private List<String> employeeIds;
}