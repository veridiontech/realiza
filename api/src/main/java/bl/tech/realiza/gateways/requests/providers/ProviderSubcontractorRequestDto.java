package bl.tech.realiza.gateways.requests.providers;

import lombok.Data;

import java.util.List;

@Data
public class ProviderSubcontractorRequestDto {
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private String supplier;
    private Boolean isActive;
    private List<String> branches;
}
