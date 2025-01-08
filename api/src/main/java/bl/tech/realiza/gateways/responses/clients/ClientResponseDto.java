package bl.tech.realiza.gateways.responses.clients;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ClientResponseDto {
    private String idClient;
    private String cnpj;
    private String tradeName;
    private String companyName;
    private String email;
    private String telephone;
    private String staff;
    private String customers;
}
