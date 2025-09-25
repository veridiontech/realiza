package bl.tech.realiza.gateways.responses.services.iaAditionalPrompt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IaAdditionalPromptNameListResponseDto {
    private String id;
    private String documentTitle;
}
