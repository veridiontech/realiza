package bl.tech.realiza.gateways.requests.services.email;

import bl.tech.realiza.domains.providers.Provider;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailNewUserRequestDto {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    @NotEmpty
    private String nameUser;
}
