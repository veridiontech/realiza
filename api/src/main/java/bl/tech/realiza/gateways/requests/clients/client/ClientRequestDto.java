package bl.tech.realiza.gateways.requests.clients.client;

import lombok.Data;

@Data
public class ClientRequestDto {
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private Boolean isUltragaz;
}
