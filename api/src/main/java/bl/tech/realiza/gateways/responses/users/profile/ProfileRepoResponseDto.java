package bl.tech.realiza.gateways.responses.users.profile;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProfileRepoResponseDto {
    private String id;
    private String name;
    private String description;
    private Boolean admin;
    private String clientId;
}
