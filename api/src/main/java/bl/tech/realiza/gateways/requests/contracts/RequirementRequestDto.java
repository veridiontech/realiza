package bl.tech.realiza.gateways.requests.contracts;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RequirementRequestDto {
    private String idRequirement;
    private String title;
    private Boolean isActive;
}
