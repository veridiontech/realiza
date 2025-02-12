package bl.tech.realiza.gateways.requests.clients.branch;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BranchUpdateRequestDto {
    private String name;
    private String cnpj;
    private String email;
    private String telephone;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private List<String> documents;
}
