package bl.tech.realiza.gateways.requests.clients;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ClientRequestDto {
    private String idClient;
    private String cnpj;
    private String tradeName;
    private String companyName;
    private String email;
    private String telephone;
    private String staff;
    private String customers;
    private Boolean isActive;
}
