package bl.tech.realiza.gateways.requests.contracts;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequirementRequestDto {
    private String idRequirement;
    @NotEmpty
    private String title;
    private Boolean isActive;
}
