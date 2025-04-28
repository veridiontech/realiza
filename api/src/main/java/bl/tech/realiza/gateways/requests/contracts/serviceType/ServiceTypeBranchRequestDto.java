package bl.tech.realiza.gateways.requests.contracts.serviceType;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ServiceTypeBranchRequestDto extends ServiceTypeBaseRequestDto {
    @NotEmpty
    private String idBranch;
}
