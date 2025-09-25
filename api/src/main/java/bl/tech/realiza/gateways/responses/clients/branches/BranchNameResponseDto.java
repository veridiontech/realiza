package bl.tech.realiza.gateways.responses.clients.branches;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BranchNameResponseDto {
    private String id;
    private String name;
}
