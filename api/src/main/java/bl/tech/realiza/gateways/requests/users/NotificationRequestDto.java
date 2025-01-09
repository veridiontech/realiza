package bl.tech.realiza.gateways.requests.users;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationRequestDto {
    private String idNotification;
    @NotEmpty
    private String title;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean read;
    @NotEmpty
    private String user;
}
