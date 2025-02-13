package bl.tech.realiza.gateways.responses.users;

import bl.tech.realiza.domains.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.TimeZone;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserResponseDto {
    // user
    private String idUser;
    private String cpf;
    private String description;
    private String password;
    private String position;
    private User.Role role;
    private String firstName;
    private TimeZone timeZone;
    private String surname;
    private String profilePictureId;
    private byte[] profilePictureData;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
    private List<String> branches;

    // client
    private String branch;

    // subcontractor
    private String subcontractor;

    // supplier
    private String supplier;
}
