package bl.tech.realiza.gateways.responses.clients;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BranchRequestDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String client;
}
