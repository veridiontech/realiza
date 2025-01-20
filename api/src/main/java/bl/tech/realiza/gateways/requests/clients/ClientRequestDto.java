package bl.tech.realiza.gateways.requests.clients;

import lombok.Data;

@Data
public class ClientRequestDto {
    private String idClient;
    private String cnpj;
    private String tradeName;
    private String fantasyName;
    private String companyName;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private Boolean isActive;
}
