package bl.tech.realiza.gateways.requests.clients;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactRequestDto {
    private String idContact;
    @NotEmpty
    private String department;
    @NotEmpty
    private String email;
    @NotEmpty
    private String country;
    @NotEmpty
    private String telephone;
    @NotNull
    private Boolean mainContact;
    @NotEmpty
    private String client;
}
