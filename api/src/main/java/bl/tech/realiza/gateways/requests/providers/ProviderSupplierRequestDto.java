package bl.tech.realiza.gateways.requests.providers;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ProviderSupplierRequestDto {
    private String idProvider;
    private String cnpj;
    private String companyName;
    private String tradeName;
    private String fantasyName;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private String client;
    private Boolean isActive;
}
