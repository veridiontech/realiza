package bl.tech.realiza.gateways.requests.users;

import bl.tech.realiza.domains.user.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProviderSupplierRequestDto {
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
    private String supplier;

    public enum Role {
        ROLE_SUPPLIER_RESPONSIBLE,
        ROLE_SUPPLIER_MANAGER,
        ROLE_VIEWER
    }
}
