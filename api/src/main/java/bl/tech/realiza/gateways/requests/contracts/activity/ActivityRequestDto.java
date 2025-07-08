package bl.tech.realiza.gateways.requests.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import lombok.Data;

import java.util.List;

@Data
public class ActivityRequestDto {
    private String title;
    private Activity.Risk risk;
    private String idBranch;
    private List<String> branchIds;
}
