package bl.tech.realiza.gateways.responses.users.profile;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileNameResponseDto {
    private String id;
    private String profileName;
}
