package bl.tech.realiza.gateways.responses.contracts;

import bl.tech.realiza.domains.contracts.Activity;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ActivityResponseDto {
    private String idActivity;
    private String title;
}
