package bl.tech.realiza.gateways.requests.services;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    @NotEmpty
    private String newPassword;
}
