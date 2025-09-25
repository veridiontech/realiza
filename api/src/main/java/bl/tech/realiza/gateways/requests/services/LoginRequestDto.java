package bl.tech.realiza.gateways.requests.services;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
}
