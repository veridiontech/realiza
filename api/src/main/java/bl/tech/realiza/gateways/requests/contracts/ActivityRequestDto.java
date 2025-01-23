package bl.tech.realiza.gateways.requests.contracts;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ActivityRequestDto {
    private String title;
    private Boolean isActive;
}
