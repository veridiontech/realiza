package bl.tech.realiza.gateways.responses.clients;

import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.TimeZone;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ClientAndUserClientResponseDto {
    private String idClient;
    private String cnpj;
    private String tradeName;
    private String companyName;
    private String staff;
    private String customers;
    private String idUser;
    private String cpf;
    private String description;
    private String password;
    private String position;
    private User.Role role;
    private String firstName;
    private TimeZone timeZone;
    private String surname;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
}
