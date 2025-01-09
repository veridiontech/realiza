package bl.tech.realiza.gateways.requests.clients;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class BranchRequestDto {
    private String idBranch;
    @NotEmpty
    private String name;
    @NotEmpty
    private String client;
}
