package bl.tech.realiza.gateways.responses.contracts.activity;

import bl.tech.realiza.domains.contract.activity.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActivityResponseDto {
    private String idActivity;
    private String title;
    private Activity.Risk risk;
}
