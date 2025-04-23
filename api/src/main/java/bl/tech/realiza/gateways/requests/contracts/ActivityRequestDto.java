package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.activity.Activity;
import lombok.Data;

@Data
public class ActivityRequestDto {
    private String title;
    private Activity.Risk risk;
    private String idBranch;
}
