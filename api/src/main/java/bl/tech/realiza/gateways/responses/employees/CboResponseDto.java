package bl.tech.realiza.gateways.responses.employees;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CboResponseDto {
    private String id;
    private String code;
    private String title;
}
