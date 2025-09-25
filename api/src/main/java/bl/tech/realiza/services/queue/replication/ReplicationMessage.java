package bl.tech.realiza.services.queue.replication;

import bl.tech.realiza.domains.enums.RiskEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.*;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplicationMessage implements Serializable {
    private String type;
    private List<String> branchIds;
    private String activityId;
    private String serviceTypeBranchId;
    private String documentId;
    private String title;
    private RiskEnum activityRisk;
    private RiskEnum serviceTypeRisk;
}
