package bl.tech.realiza.gateways.responses.services;

import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EnterpriseAndUserResponseDto {
    private String idEnterprise;
    private String cnpj;
    private String tradeName;
    private String corporateName;
    private String email;
    private String phone;

    private String idUser;
    private String cpf;
    private String name;
    private String surname;
    private String position;
    private User.Role role;
    private String password;
}
