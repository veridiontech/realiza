package bl.tech.realiza.gateways.responses.contracts;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RequirementResponseDto {
    private String idRequirement;
    private String title;
}
