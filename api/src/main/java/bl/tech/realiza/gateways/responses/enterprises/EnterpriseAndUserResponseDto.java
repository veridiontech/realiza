package bl.tech.realiza.gateways.responses.enterprises;

import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EnterpriseAndUserResponseDto {
    private String idClient;
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String staff;
    private String customers;
    private String idUser;
    private String cpf;
    private String description;
    private String password;
    private String position;
    private User.Role role;
    private String firstName;
    private String surname;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
}
