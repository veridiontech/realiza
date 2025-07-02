package bl.tech.realiza.gateways.responses.users;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileNameResponseDto {
    private String id;
    private String profileName;
}
