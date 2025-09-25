package bl.tech.realiza.gateways.responses.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotificationResponseDto {
    private String idNotification;
    private String title;
    private String description;
    private Boolean isRead;
    private String user;
}
