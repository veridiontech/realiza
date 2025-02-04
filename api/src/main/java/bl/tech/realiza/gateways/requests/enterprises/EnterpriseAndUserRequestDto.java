package bl.tech.realiza.gateways.requests.enterprises;

import bl.tech.realiza.domains.providers.Provider;
import bl.tech.realiza.domains.user.User;
import lombok.Data;

@Data
public class EnterpriseAndUserRequestDto {
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String email;
    private String phone;
    private String cpf;
    private String name;
    private String surname;
    private String position;
    private User.Role role;
    private String password;
    private String idCompany;
    private Provider.Company company;
}
