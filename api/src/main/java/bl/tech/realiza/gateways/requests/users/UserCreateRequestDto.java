package bl.tech.realiza.gateways.requests.users;

import bl.tech.realiza.domains.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserCreateRequestDto {
    @NotEmpty
    private String cpf;
    private String description;
    @NotEmpty
    private String password;
    private String position;
    @NotNull
    private User.Role role;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String surname;
    @NotEmpty
    private String email;
    private String telephone;
    private String cellphone;
    @NotNull
    private Enterprise enterprise;
    private String idEnterprise;

    public enum Enterprise {
        REALIZA,
        CLIENT,
        SUPPLIER,
        SUBCONTRACTOR
    }
}
