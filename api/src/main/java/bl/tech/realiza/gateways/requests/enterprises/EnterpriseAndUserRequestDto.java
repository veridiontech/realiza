package bl.tech.realiza.gateways.requests.enterprises;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseAndUserRequestDto {
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String cep;
    private String state;
    private String city;
    private String address;
    private String number;
    private String email;
    private String phone;
    @NotEmpty
    private String cpf;
    @NotEmpty
    private String name;
    @NotEmpty
    private String surname;
    private String position;
    private User.Role role;
    @NotEmpty
    private String password;
    @NotEmpty
    private String idCompany; // id da empresa que vai ser criada
    private Provider.Company company;
}
