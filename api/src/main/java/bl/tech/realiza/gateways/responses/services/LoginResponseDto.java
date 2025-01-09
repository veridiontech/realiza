package bl.tech.realiza.gateways.responses.services;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDto {
    private String token;
}
