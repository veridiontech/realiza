package bl.tech.realiza.gateways.requests.services.iaAdditionalPrompt;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IaAdditionalPromptRequestDto {
    private String documentId;
    @Size(max = 1000)
    private String description;
}
