package bl.tech.realiza.gateways.responses.services.iaAditionalPrompt;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IaAdditionalPromptResponseDto {
    private String id;
    private String description;
    private String documentId;
    private String documentTitle;
}
