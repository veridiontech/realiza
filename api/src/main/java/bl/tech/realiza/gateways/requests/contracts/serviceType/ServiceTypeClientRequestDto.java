package bl.tech.realiza.gateways.requests.contracts.serviceType;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceTypeClientRequestDto extends ServiceTypeBaseRequestDto {
    @NotEmpty
    private String idClient;
}
