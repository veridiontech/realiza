package bl.tech.realiza.gateways.responses.users;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDto {
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean read;
    @NotEmpty
    private String user;
}
