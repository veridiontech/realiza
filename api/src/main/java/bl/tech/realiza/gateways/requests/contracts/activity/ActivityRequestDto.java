package bl.tech.realiza.gateways.requests.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.enums.RiskEnum;
import lombok.Data;

import java.util.List;

@Data
public class ActivityRequestDto {
    private String title;
    private RiskEnum risk;
    private String idBranch;
    private List<String> branchIds;
}
