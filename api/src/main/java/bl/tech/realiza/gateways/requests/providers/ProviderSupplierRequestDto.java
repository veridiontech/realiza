package bl.tech.realiza.gateways.requests.providers;

import lombok.Builder;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderSupplierRequestDto {
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String email;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private List<String> branches;
}
