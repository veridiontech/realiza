package bl.tech.realiza.gateways.responses.clients.branches;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BranchNameResponseDto {
    private String id;
    private String name;
}
