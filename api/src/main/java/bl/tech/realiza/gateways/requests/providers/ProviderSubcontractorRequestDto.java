package bl.tech.realiza.gateways.requests.providers;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
    private List<String> branches;
}
