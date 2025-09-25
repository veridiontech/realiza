package bl.tech.realiza.gateways.requests.services.email;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailRegistrationDeniedRequestDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String responsibleName;
    @NotEmpty
    private String enterpriseName;
    @NotEmpty
    private String reason;
}
