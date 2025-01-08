package bl.tech.realiza.gateways.responses.contracts;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequirementRequestDto {
    @NotEmpty
    private String title;
}
