package bl.tech.realiza.gateways.requests.employees;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CboRequestDto {
    @NotEmpty
    private String code;
    @NotEmpty
    private String title;
}
