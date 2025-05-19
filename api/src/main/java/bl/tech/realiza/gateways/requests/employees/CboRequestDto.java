package bl.tech.realiza.gateways.requests.employees;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CboRequestDto {
    @NotEmpty
    private String code;
    @NotEmpty
    private String title;
}
