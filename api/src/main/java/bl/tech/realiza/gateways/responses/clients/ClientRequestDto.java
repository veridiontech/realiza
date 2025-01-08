package bl.tech.realiza.gateways.responses.clients;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ClientRequestDto {
    @NotEmpty
    private String cnpj;
    @NotEmpty
    private String tradeName;
    @NotEmpty
    private String companyName;
    @NotEmpty
    private String email;
    @NotEmpty
    private String telephone;
    @NotEmpty
    private String staff;
    @NotEmpty
    private String customers;
}
