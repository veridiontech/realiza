package bl.tech.realiza.gateways.requests.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.enums.RiskEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ServiceTypeRequestDto {
    @NotEmpty
    private String title;
    @NotNull
    private RiskEnum risk;
    private String branchId;
    private List<String> branchIds;
}
