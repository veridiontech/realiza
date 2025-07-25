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
    private String corporateName;
    private String logoSignedUrl;
    private String telephone;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private Boolean isUltragaz;
}
