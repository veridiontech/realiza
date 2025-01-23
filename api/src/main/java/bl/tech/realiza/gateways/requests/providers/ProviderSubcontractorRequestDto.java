package bl.tech.realiza.gateways.requests.providers;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProviderSubcontractorRequestDto {
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
    private String supplier;
    private Boolean isActive;
    private List<String> branches;
}
