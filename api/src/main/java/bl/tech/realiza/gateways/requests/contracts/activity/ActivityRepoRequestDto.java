package bl.tech.realiza.gateways.requests.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.enums.RiskEnum;
import lombok.Data;

@Data
public class ActivityRepoRequestDto {
    private String title;
    private RiskEnum risk;
}
