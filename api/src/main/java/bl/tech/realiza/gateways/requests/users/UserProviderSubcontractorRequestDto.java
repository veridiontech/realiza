package bl.tech.realiza.gateways.requests.users;

import bl.tech.realiza.domains.user.User;
import lombok.Data;

@Data
public class UserProviderSubcontractorRequestDto {
    private String cpf;
    private String description;
    private String password;
    private String newPassword;
    private String position;
    private User.Role role;
    private String firstName;
    private String surname;
    private String email;
    private String profilePicture;
    private String telephone;
    private String cellphone;
    private String subcontractor;

    public enum Role {
        ROLE_SUBCONTRACTOR_RESPONSIBLE,
        ROLE_SUBCONTRACTOR_MANAGER,
        ROLE_VIEWER
    }
}
