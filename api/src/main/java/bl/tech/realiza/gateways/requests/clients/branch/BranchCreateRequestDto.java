package bl.tech.realiza.gateways.requests.clients.branch;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class BranchCreateRequestDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String cnpj;
    @NotEmpty
    private String email;
    @NotEmpty
    private String telephone;
    @NotEmpty
    private String cep;
    @NotEmpty
    private String state;
    @NotEmpty
    private String city;
    @NotEmpty
    private String address;
    @NotEmpty
    private String number;
    private String client;
    private List<String> center;
    private List<String> documents;
}
