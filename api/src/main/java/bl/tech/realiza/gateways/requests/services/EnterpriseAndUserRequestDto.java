package bl.tech.realiza.gateways.requests.services;

import bl.tech.realiza.domains.user.User;
import lombok.Data;

@Data
public class EnterpriseAndUserRequestDto {
    private String cnpj;
    private String nameEnterprise;
    private String fantasyName;
    private String socialReason;
    private String email;
    private String phone;
    private String cpf;
    private String name;
    private String surname;
    private String position;
    private User.Role role;
    private String password;
    private String idCompany;
    private EmailRequestDto.Company company;
}
