package bl.tech.realiza.gateways.responses.services.iaAditionalPrompt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class IaAdditionalPromptNameListResponseDto {
    private String id;
    private String documentTitle;
}
