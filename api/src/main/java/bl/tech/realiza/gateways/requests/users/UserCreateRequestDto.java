package bl.tech.realiza.gateways.requests.users;

import bl.tech.realiza.domains.user.User;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import java.util.List;

@Data
public class UserCreateRequestDto {
    @NotEmpty @CPF
    private String cpf;
    private String description;
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

    private String profileId;
    private List<String> branchAccessIds;
    private List<String> contractAccessIds;

    public enum Enterprise {
        REALIZA,
        CLIENT,
        SUPPLIER,
        SUBCONTRACTOR
    }
}
