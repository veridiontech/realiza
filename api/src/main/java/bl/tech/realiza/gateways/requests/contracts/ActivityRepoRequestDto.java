package bl.tech.realiza.gateways.requests.contracts;

import bl.tech.realiza.domains.contract.Activity;
import lombok.Data;

@Data
public class ActivityRepoRequestDto {
    private String title;
    private Activity.Risk risk;
}
