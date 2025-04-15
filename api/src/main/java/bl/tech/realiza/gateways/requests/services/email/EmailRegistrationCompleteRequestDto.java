package bl.tech.realiza.gateways.requests.services.email;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRegistrationCompleteRequestDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String responsibleName;
    @NotEmpty
    private String parentEnterpriseName;
}
