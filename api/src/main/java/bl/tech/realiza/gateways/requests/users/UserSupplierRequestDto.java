package bl.tech.realiza.gateways.requests.users;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.TimeZone;

@Data
public class UserSupplierRequestDto {
    @NotEmpty
    private String cpf;
    @NotEmpty
    private String description;
    @NotEmpty
    private String password;
    @NotEmpty
    private String position;
    @NotEmpty
    private String role;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private TimeZone timeZone;
    @NotEmpty
    private String surname;
    @NotEmpty
    private String email;
    @NotEmpty
    private String profilePicture;
    @NotEmpty
    private String telephone;
    @NotEmpty
    private String cellphone;
    @NotEmpty
    private String supplier;
}
