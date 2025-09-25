package bl.tech.realiza.gateways.requests.users;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDto {
    private String title;
    private String description;
    private Boolean isRead;
    private String user;
}
