package bl.tech.realiza.gateways.requests.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import bl.tech.realiza.domains.enums.RiskEnum;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRepoRequestDto {
    private String title;
    private RiskEnum risk;
}
