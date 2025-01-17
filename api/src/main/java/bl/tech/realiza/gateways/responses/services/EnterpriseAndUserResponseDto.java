package bl.tech.realiza.gateways.responses.services;

import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.TimeZone;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EnterpriseAndUserResponseDto {
    private String idEnterprise;
    private String cnpj;
    private String nameEnterprise;
    private String fantasyName;
    private String socialReason;
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
