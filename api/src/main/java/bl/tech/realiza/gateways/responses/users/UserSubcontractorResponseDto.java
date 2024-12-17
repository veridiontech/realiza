package bl.tech.realiza.gateways.responses.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.TimeZone;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserSubcontractorResponseDto {
    private String idUser;
    private String cpf;
    private String description;
    private String password;
    private String position;
    private String role;
    private String firstName;
    private TimeZone timeZone;
    private String surname;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
    private String subcontractor;
}
