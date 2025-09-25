package bl.tech.realiza.gateways.requests.services;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequestDto {
    @NotEmpty
    private String newPassword;
}
