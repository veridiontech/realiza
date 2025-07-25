package bl.tech.realiza.gateways.requests.users;

import bl.tech.realiza.domains.user.User;
import lombok.Data;

import java.util.List;

@Data
public class UserClientRequestDto {
    private String cpf;
    private String description;
    private String password;
    private String newPassword;
    private String position;
    private User.Role role;
    private String firstName;
    private String surname;
    private String email;
    private String telephone;
    private String cellphone;
    private String branch;
    private String idUser;

    private String profileId;
    private List<String> branchAccessIds;
    private List<String> contractAccessIds;

    public enum Role {
        ROLE_CLIENT_RESPONSIBLE,
        ROLE_CLIENT_MANAGER,
        ROLE_VIEWER
    }
}
