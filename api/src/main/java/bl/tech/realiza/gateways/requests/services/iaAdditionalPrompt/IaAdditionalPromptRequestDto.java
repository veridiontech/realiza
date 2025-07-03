package bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IaAdditionalPromptRequestDto {
    private String documentId;
    @Size(max = 1000)
    private String description;
}
