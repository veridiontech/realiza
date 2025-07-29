package bl.tech.realiza.gateways.responses.contracts.serviceType;

import bl.tech.realiza.domains.contract.serviceType.ServiceType;
import bl.tech.realiza.domains.enums.RiskEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServiceTypeFullResponseDto {
    private String idServiceType;
    private String title;
    private RiskEnum risk;
    private String branchId;
    private String clientId;
    private LocalDateTime createdAt;
}
