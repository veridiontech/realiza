package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contract.activity.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActivityRepoResponseDto {
    private String idActivity;
    private String title;
    private Activity.Risk risk;
}
